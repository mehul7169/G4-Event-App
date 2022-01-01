
/*Functions Related to Individuals*/

/*
* Method for creating a new instance of Individual .
* Queries doc for necessary params for post request.
 */
function addIndividual() {

    //clump all selected tags into csv string
    var selected = [];
    for (var option of document.getElementById('tags').options) {
        if (option.selected) {
            selected.push(option.value);
        }
    }
    let tags = "";
    for (let i = 0; i < selected.length; i++) {
        tags += selected[i];
        tags += ', ';
    }
    tags = tags.substring(0, tags.length - 2);

    //get other params from doc and check that they're valid
    const name = document.getElementById("name");
    if (!validateName(name.value)) { return false; }
    const email = document.getElementById("email");
    if (!validateEmail(email.value)) { return false; }

    //send post request
    fetch('http://localhost:7000/individuals?name=' + name.value +
        '&email=' + email.value + '&tags=' + tags,
        {
            method: 'Post',
        }
    ).then(res => window.location.reload = window.location.reload(true));
}

/*
* Method for updating an individual's content tags.
* Queries doc for necessary information for sending update request.
 */
function updateIndiv() {

    //get selected tags and clump into csv string
    var selected = [];
    for (var option of document.getElementById('tagsEdit').options) {
        if (option.selected) {
            selected.push(option.value);
        }
    }
    let tags = "";
    for (let i = 0; i < selected.length; i++) {
        tags += selected[i];
        tags += ', ';
    }
    tags = tags.substring(0, tags.length - 2);

    //send update request
    fetch('http://localhost:7000/individualPage?tags=' + tags, {
            method: 'Put',
        }
    ).then(res => window.location.reload = window.location.reload(true));

}

/*
* Checks that supplied name (for create individual form) is valid (contians only letters).
* return: true if valid, false otherwise (and displays alert)
 */
function validateName(name) {
    const re = /^[A-Za-z\s]+$/;
    if(re.test(name)) {
        return true;
    }
    alert("Names must contain only letters and spaces!");
    return false;
}

/*
* Checks that supplied email (for create individual form) is valid.
* return: true if valid, false otherwise (and displays alert)
 */
function validateEmail(email) {
    const re = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    if(re.test(email.toLowerCase())) {
        return true;
    }
    alert("Please enter a valid email!");
    return false;
}
