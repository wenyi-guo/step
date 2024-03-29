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

/** Global variables */
var number = 5; // default max number of comments in one page
var order = "descending"; // default order
var pageClass;
var pagination;

/** The functions called onload */
function start(page){
    switch(page) {
        case "index":
            break;
        case "projects":
            break;
        case "gallery":
            setupGeoChart();
            break;
        case "comments":
            createPagination();
            getOrder();
            getData(5);
            isLoggedIn();
            setupBarChart(); 
            break;
        default:
            break;
    }
   
}

/** Get the order from user select. */
function getOrder(){
    const myorders = document.getElementById("orders");
    myorders.addEventListener("change", function(){
        var e = document.getElementById("orders");
        var selectedOrder = e.options[e.selectedIndex].value;
        if (selectedOrder === "descending") {
            order = "descending";
        }
        else{
            order = "ascending";
        }
        getData(number);
    });
}


/** Set the number of comments to view. */
function setNum(num){
    number = num.value;
    getData(number);
}

/** Fetch the data from server then add it to the page. */
function getData(num) {
  // Get the page number selected by pagination
  var pageString;
  for (i = 0; i < pagination.length; i++) {
    var page = pagination[i];
    if(page.classList.contains('active')){
        pageString = page.innerHTML;
    }
  }
  fetch('/get-comments?num=' + num + '&page=' + pageString + '&order=' + order).then(response => response.json()).then((comments) =>  {
    const commentListElement = document.getElementById('comments-container');
    commentListElement.innerHTML="";
    if(comments.length === 0){
        commentListElement.innerText="No comment on this page.";   
    } else{
        comments.forEach((comment) => {
            createListElement(comment).then((commentElement) => {
                commentListElement.appendChild(commentElement);
            })
        })
    }
  });
}

/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('div');
  liElement.classList.add('row');
  liElement.classList.add('card');
  var cardTitle = document.createElement('div');
  cardTitle.className = 'card-title';
  cardTitle.innerText = "User: " + text.userName;
  var cardBody = document.createElement('div');
  cardBody.className = 'card-body';
  var contentElement = document.createElement('div');
  contentElement.classList.add('row');
  var date = new Date(text.timestamp);
  var score;
  return getScore(text.content).then((result)  => {
    score = result.toFixed(2);
    var emoji;
    if(score >= 0.6){
        emoji = "🥰";
    } else if(score >= 0.2){
        emoji = "😊";
    } else if(score >= -0.2){
        emoji = "😐";
    } else if(score >= -0.6){
        emoji = "🙁";
    } else{
        emoji = "😭";
    }
    contentElement.innerText = "Email: " + text.email + "\nTime: " + date.toLocaleString() + "\n" + "Comment: " + text.content + "\nScore: " + score + " " + emoji;
    var deleteBtn = document.createElement("BUTTON");   // Create a <button> element
    deleteBtn.classList.add('row');
    deleteBtn.classList.add('close-button');
    deleteBtn.innerHTML = "delete";   
    deleteBtn.onclick = function() { 
        fetch("/delete-id?id=" + text.id, {method: 'POST'})
            .then(response => {
                if (response.status === 200) {
                console.log("delete one comment");
                getData(5);
                return response.json();
                } else {
                console.log("fail");
                throw new Error('Something went wrong on api server!');
                }
            });
        }
    cardBody.appendChild(cardTitle);
    cardBody.appendChild(contentElement);
    cardBody.appendChild(deleteBtn);
    liElement.appendChild(cardBody);
    return liElement;
    });  
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
      setupBarChart();
      return response.json();
    } else {
      console.log("fail");
      throw new Error('Something went wrong on api server!');
    }
  });
}


function createPagination() {
    pageClass = document.getElementsByClassName("pagination")[0];
    pagination = document.getElementsByClassName("pagination")[0].children;
    changePage();
}

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

/** Set up the chart API and call the drawcharts function. */
function setupBarChart() {
    /** Google Chart API. */
    google.charts.load('current', {
            'packages':['corechart'],
        });
    google.charts.setOnLoadCallback(drawBarChart);
}

function setupGeoChart() {
    /** Google Chart API. */
    google.charts.load('current', {
            'packages':['corechart', 'geochart'],
            'mapsApiKey': 'AIzaSyB9U6xT4W3gkESOvA8Sn_kU_aCPZjTh1f4'
        });
    google.charts.setOnLoadCallback(drawGeoChart);
}

function drawBarChart() {
    fetch('/get-all-comments').then(response => response.json())
    .then((comments) => {
        const data = new google.visualization.DataTable();
        data.addColumn('string', 'Username');
        data.addColumn('number', 'Number of Comments');
        comments.forEach((comment) => {
            var row = data.getFilteredRows([{column: 0, value: comment.userName}])
            if(row === undefined || row === null || row.length == 0){
                data.addRow([comment.userName, 1]);
            }
            else{
                data.setCell(row[0], 1, data.getValue(row[0], 1) +1);
            }
        })

    var options = {
          title: 'Comments Bar Chart',
          width: 600,
          height: 320,
          legend: { position: 'none' },
          chart: { title: 'Number of Comments by User Name',},
          bars: 'horizontal', // Required for Material Bar Charts.
          axes: {
            x: {
              0: { side: 'top', label: 'Number of Comments'} // Top x-axis.
            }
          },
          bar: { groupWidth: "90%" },
          style: {opacity: 0.5}
        };

    var view = new google.visualization.DataView(data);
    view.setColumns([0, 1,
                       { calc: "stringify",
                         sourceColumn: 1,
                         type: "string",
                         role: "annotation" },
                       ]);

    var chart = new google.visualization.BarChart(document.getElementById("barchart"));
    chart.draw(view, options);
    });
}

function drawGeoChart() {
    var data2 = google.visualization.arrayToDataTable([
        ['Country', 'Been to'],
        ['United States', 1],
        ['China', 1],
        ['Japan', 1],
        ['Canada', 1],
        ['France', 1],
        ['Italy', 1],
        ['United Kingdom', 1],
        ['Australia', 1],
    ]);

    var options2 = {
         width: 500,
        height: 300,
    };

    var chart2 = new google.visualization.GeoChart(document.getElementById('geochart'));
    chart2.draw(data2, options2);
}

/** Draw bar chart and geo chart. */
function drawCharts() {
    fetch('/get-all-comments').then(response => response.json())
    .then((comments) => {
        const data = new google.visualization.DataTable();
        data.addColumn('string', 'Username');
        data.addColumn('number', 'Number of Comments');
        comments.forEach((comment) => {
            var row = data.getFilteredRows([{column: 0, value: comment.userName}])
            if(row === undefined || row === null || row.length == 0){
                data.addRow([comment.userName, 1]);
            }
            else{
                data.setCell(row[0], 1, data.getValue(row[0], 1) +1);
            }
        })

    var options = {
          title: 'Comments Bar Chart',
          width: 600,
          height: 320,
          legend: { position: 'none' },
          chart: { title: 'Number of Comments by User Name',},
          bars: 'horizontal', // Required for Material Bar Charts.
          axes: {
            x: {
              0: { side: 'top', label: 'Number of Comments'} // Top x-axis.
            }
          },
          bar: { groupWidth: "90%" },
          style: {opacity: 0.5}
        };

    var view = new google.visualization.DataView(data);
    view.setColumns([0, 1,
                       { calc: "stringify",
                         sourceColumn: 1,
                         type: "string",
                         role: "annotation" },
                       ]);

    var chart = new google.visualization.BarChart(document.getElementById("barchart"));
    chart.draw(view, options);
    
    // geo chart
    var data2 = google.visualization.arrayToDataTable([
        ['Country', 'Been to'],
        ['United States', 1],
        ['China', 1],
        ['Japan', 1],
        ['Canada', 1],
        ['France', 1],
        ['Italy', 1],
        ['United Kingdom', 1],
        ['Australia', 1],
    ]);

    var options2 = {
        width: 1000,
        height: 750,
    };

    var chart2 = new google.visualization.GeoChart(document.getElementById('geochart'));
    chart2.draw(data2, options2);
  });
}

/** Get the login status. If logged in: display post comment box and logout button. If not logged in: display login button and hide post comment box. */
function isLoggedIn(){
    fetch('/auth', {headers : { 
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            }
        }).then(response => response.json()).then((loginstatus) =>  {
        console.log(loginstatus);
        if(loginstatus.loginStatus === true){
            var commentElement = document.getElementById('post-comment-container');
            commentElement.style.visibility = "visible";
            var loginElement = document.getElementById('login-container');
            loginElement.style.visibility = "hidden";
            var logoutElement = document.getElementById('logout-container');
            logoutElement.style.visibility = "visible";
            var logoutURL = document.getElementById('logoutURL');
            logoutURL.href = loginstatus.URL;
        }
        else{
            var commentElement = document.getElementById('post-comment-container');
            commentElement.style.visibility = "hidden";
            var loginElement = document.getElementById('login-container');
            loginElement.style.visibility = "visible";
            var logoutElement = document.getElementById('logout-container');
            logoutElement.style.visibility = "hidden";
            var loginURL = document.getElementById('loginURL');
            loginURL.href = loginstatus.URL;
        }
    });
}

function getScore(message, callback){
    return fetch('/sentiment?message=' + message, {method: 'POST'})
        .then(response => {
            return response.json().then((result) =>  {
                return result.score;
            })
            .catch(error => {
                console.warn(error);
            })
        }); 
}


