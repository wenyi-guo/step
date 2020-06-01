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


var number = 5;
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
 * Set the number of comments to view
 */
function setNum(num){
    number = num.value;
    getData(number);
}
/**
 * Fetch the data from server then add it to the page.
 */
function getData(num) {
  // Get the page number selected by pagination
  var pageString;
  for (i = 0; i < pagination.length; i++) {
    var page = pagination[i];
    if(page.classList.contains('active')){
        pageString = page.innerHTML;
    }
  }
  fetch('/get-comments?num=' + num + '&page=' + pageString).then(response => response.json()).then((comments) =>  {
    console.log(comments);
    const commentListElement = document.getElementById('comments-container');
    commentListElement.innerHTML="";
    if(comments.length === 0){
        commentListElement.innerText="No comment on this page.";   
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
  liElement.innerText = "\nUser Name: " + text.userName + "   Email: " + text.email + "\nTime: " + text.timestamp + "\n" + "Comment: " + text.content + "\n";
 
  return liElement;
}

/** Create a new page button. */
function createPageElement(newnum){
    const element = document.createElement('button');
    element.classList.add('pagebtn');
    element.innerText = newnum;

    return element;
}

function deleteData(){
fetch("delete-comments", {method: 'POST'})
  .then(response => {
    if (response.status === 200) {
      console.log("success");
      getData(5);
      return response.json();
    } else {
      console.log("fail");
      throw new Error('Something went wrong on api server!');
    }
  });
}

var pageClass = document.getElementsByClassName("pagination")[0];
var pagination = document.getElementsByClassName("pagination")[0].children;

// handle prev 
function prev(){
    if(pagination[1].innerText === '1'){
        return;
    }
    var current = document.querySelector(".pagebtn.active");
    current.classList.remove('active');
    if(current === pagination[1]){
        const first = parseInt(pagination[1].innerText) - 1;   
        pageClass.removeChild(pagination[5]);
        pageClass.insertBefore(createPageElement(first.toString()), pageClass.children[1]);
        document.getElementsByClassName("pagination")[0].children[1].classList.add("active");
    }
    var prevElement = current.previousElementSibling;
    prevElement.classList.add("active");
    const first = parseInt(pagination[1].innerText) - 1;   
    if(first <= 0){
        return;
    }
    pageClass.removeChild(pagination[5]);
    pageClass.insertBefore(createPageElement(first.toString()), pageClass.children[1]);
    // refresh the pagination variables
    pageClass = document.getElementsByClassName("pagination")[0];
    pagination = document.getElementsByClassName("pagination")[0].children;
    changePage();
    getData(number);
}

// handle next
function next(){
    var current = document.querySelector(".pagebtn.active");
    current.classList.remove('active');
    if(current === pagination[5]){
        const last = parseInt(pagination[5].innerText) + 1;   
        pageClass.removeChild(pagination[1]);
        pageClass.insertBefore(createPageElement(last.toString()), pageClass.children[5]);
        document.getElementsByClassName("pagination")[0].children[5].classList.add("active");
    }
    var nextElement = current.nextElementSibling;
    nextElement.classList.add("active");
    const last = parseInt(pagination[5].innerText) + 1;   
    pageClass.removeChild(pagination[1]);
    pageClass.insertBefore(createPageElement(last.toString()), pageClass.children[5]);
    // refresh the pagination variables
    pageClass = document.getElementsByClassName("pagination")[0];
    pagination = document.getElementsByClassName("pagination")[0].children;
    changePage();
    getData(number);
}

// Change the active page
function changePage(){
    for (i = 1; i < pagination.length-1; i++) {
    var page = pagination[i];
    page.addEventListener("click", function() {
        var current = document.querySelector(".pagebtn.active");
        current.classList.remove('active');
        this.classList.add('active');
        getData(number);
    });
    }
}

function start(){
    getData(5);
    changePage();
}




