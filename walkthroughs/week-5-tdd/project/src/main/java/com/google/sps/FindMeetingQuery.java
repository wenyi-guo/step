// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.*;
import java.util.Arrays;
import java.util.Collections;

    
public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Set<TimeRange> busyTimes = new HashSet<>(); // a set of busy time of all attendees, containing overlaps
    Collection<TimeRange> availables = new ArrayList<>();
    Collection<TimeRange> optionalAvailables = new ArrayList<>();
    Collection<String> attendees = request.getAttendees();
    Collection<String> optionalAttendees = request.getOptionalAttendees();
    long duration = request.getDuration();

    // fill the busyTimes with mandatory attendees' events
    Iterator<Event> eventsIterator = events.iterator();
    while(eventsIterator.hasNext()){
        Event event = eventsIterator.next();
        Set<String> eventAttendees = event.getAttendees();
        // check if the any mandatory attendee of the event is in the request attendees 
        if(Collections.disjoint(attendees, eventAttendees) == false){
            TimeRange when = event.getWhen();
            busyTimes.add(when);
        }
    }
    
    // sort the busyTimes by start time
    List<TimeRange> busyTimesList = new ArrayList<TimeRange>();
    busyTimesList.addAll(busyTimes);
    Collections.sort(busyTimesList, TimeRange.ORDER_BY_START);

    // duration too long no available slots
    if(duration > TimeRange.WHOLE_DAY.duration()){
        return Arrays.asList();
    }

    // no busy time
    if(busyTimesList.size() == 0){
        availables = Arrays.asList(TimeRange.WHOLE_DAY);
    }
    else{
        // check time before all busy times
        TimeRange first = busyTimesList.get(0);
        if(first.start() >= duration){
            availables.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, first.start(), false));
        }

        // if equal to or more than two busy times
        for(int i = 0; i < busyTimesList.size() - 1; i++){
            TimeRange firstTime = busyTimesList.get(i);
            TimeRange secondTime = busyTimesList.get(i+1);
            int diff = secondTime.start() - firstTime.end();
            if(diff <= 0 || diff < duration){
                continue;
            }
            else{
                availables.add(TimeRange.fromStartEnd(firstTime.end(), secondTime.start(), false));
            }
        }

        // check time after all busy times
        Collections.sort(busyTimesList, TimeRange.ORDER_BY_END);
        TimeRange last = busyTimesList.get(busyTimesList.size() - 1);
        if(TimeRange.END_OF_DAY - last.end() + 1 >= duration){
            availables.add(TimeRange.fromStartEnd(last.end(), TimeRange.END_OF_DAY, true));
        }
    }

    /** optimized optional attendees */
    // create an array representing the minutes for the whole day, array[i] indicates at minute i, the number of attendees who are busy
    int[] array = new int[1440]; 
    Arrays.fill(array, 0);
    Iterator<Event> newEventsIterator = events.iterator();
    while(newEventsIterator.hasNext()){
        Event event = newEventsIterator.next();
        Set<String> eventAttendees = event.getAttendees();
        Set<String> copy = new HashSet<>(eventAttendees);
        copy.retainAll(optionalAttendees);
        if(copy.size() != 0){
            for(int i = event.getWhen().start(); i < event.getWhen().end(); i++){
                array[i] = array[i] + eventAttendees.size();
            }
        }
    }

    // find slots work for both mandatory and optional attendees
    optionalAvailables = availablesByNumber(duration, array, availables, 0);

    // if slots exist for both mandatory and optional attendees
    if(optionalAvailables.size() != 0){
        return optionalAvailables;
    }
    // if slot doesn't exist for both, return optimized: allow mandatory attendees and the greatest possible number of optional attendees 
    int max = 0;
    for(int i = 0; i < array.length; i++){
        if(array[i] > max)
            max = array[i];
    }
    int k = 1;
    while(k <= max){
        optionalAvailables = availablesByNumber(duration, array, availables, k);
        if(optionalAvailables.size() != 0){
            return optionalAvailables;
        }
        k++;
    } 
    return availables;
  }

  /** The function returns all the timeslots available while n optional attendees can't attend. */
  public Collection<TimeRange> availablesByNumber(long duration, int[] array, Collection<TimeRange> availables, int n){
    Collection<TimeRange> numOptionalAvailables = new ArrayList<>();
    Iterator<TimeRange> availablesIterator = availables.iterator();
    while(availablesIterator.hasNext()){
        TimeRange available = availablesIterator.next();
        int i = available.start();
        while(i < available.end()){
            if(array[i] <= n){
                int s = i;
                while(i+1 < available.end() && array[i+1] <= n){
                    i++;
                }
                if(i - s + 1 >= duration){
                    numOptionalAvailables.add(TimeRange.fromStartEnd(s, i, true));
                }
            }
            if(i < available.end()){
                i++;
            }
        }
    }
    return numOptionalAvailables;
  }
}
