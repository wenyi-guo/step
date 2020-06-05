var pageClass = document.getElementsByClassName("pagination")[0];
var pagination = document.getElementsByClassName("pagination")[0].children;
    
    changePage();

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