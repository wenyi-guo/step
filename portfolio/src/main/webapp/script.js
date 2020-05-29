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

/**
 * Add collapse content to the collapsible buttons
 */
var collapse = document.getElementsByClassName("collapsible");
var i;
// Add collapse function to each collapsible button
for (i = 0; i < collapse.length; i++) {
  collapse[i].addEventListener("click", function() {
    this.classList.toggle("collapse-active");
    var content = this.nextElementSibling;
    if (content.style.maxHeight) {
      content.style.maxHeight = null;
    } else {
      content.style.maxHeight = content.scrollHeight + "px";
    }
  });
}

/**
 * Fetch the data from server then add it to the page.
 */
 function getData() {
  fetch('/get-comments').then(response => response.json()).then((comments) =>  {
    console.log(comments);
    const commentListElement = document.getElementById('comments-container');
    if(comments.length === 0){
        commentListElement.innerText="No comment yet.";   
    } else{
        comments.forEach((comment) => {
        commentListElement.appendChild(createListElement(comment));
        })
    }
  });
}

/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('div');
  liElement.classList.add('row');
  liElement.innerText = "User Name: " + text.userName + "   Email: " + text.email + "\n" + "Comment: " + text.content + "<br>";
 
  return liElement;
}

function deleteData(){
fetch("delete-comments", {method: 'POST'})
  .then(response => {
    if (response.status === 200) {
      console.log("success");
      getData();
      return response.json();
    } else {
      console.log("fail");
      throw new Error('Something went wrong on api server!');
    }
  });
}

