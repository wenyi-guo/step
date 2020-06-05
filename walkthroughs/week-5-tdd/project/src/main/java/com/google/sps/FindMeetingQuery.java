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
// import java.util.Iterator;
// import java.util.Collections;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.HashSet;
// import java.util.Set;
// import java.util.List;
// import java.util.Comparator


public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    //throw new UnsupportedOperationException("TODO: Implement this method.");
    Set<TimeRange> busyTimes = new HashSet<>(); // a set of busy time of all attendees, containing overlaps
    Collection<TimeRange> availables = new ArrayList<>();
    Collection<String> attendees = request.getAttendees();
    long duration = request.getDuration();

    Iterator<Event> eventsIterator = events.iterator();
    while(eventsIterator.hasNext()){
        Event event = eventsIterator.next();
        Set<String> eventAttendees = event.getAttendees();
        // check if the any attendee of the event is in the request attendees 
        if(Collections.disjoint(attendees, eventAttendees) == false){
            TimeRange when = event.getWhen();
            busyTimes.add(when);
        }
    }

    // sort the busyTimes by start time
    List<TimeRange> busyTimesList = new ArrayList<TimeRange>();
    busyTimesList.addAll(busyTimes);
    Collections.sort(busyTimesList, TimeRange.ORDER_BY_START);

    // get the available time ranges
    if(duration > TimeRange.WHOLE_DAY.duration()){
        return  Arrays.asList();
    }
    if(busyTimesList.size() == 0){
        return Arrays.asList(TimeRange.WHOLE_DAY);
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

    return availables;
  }
}
