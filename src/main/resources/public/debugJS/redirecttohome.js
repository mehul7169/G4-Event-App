
/*
* Method for redirecting after adding an individual.
 */
function addIndividualsRedirect() {

    fetch('http://localhost:7000/individuals?name='+ document.getElementById("name").value +
        'tag=' + document.getElementById("tag").value, {
        method: 'POST',
    }).then(res => window.location.href = "http://localhost:7000/")

}

/*
* Method for redirecting after adding a group.
 */
function addGroupsRedirect() {

    fetch('http://localhost:7000/groups?name='+ document.getElementById("name").value, {
        method: 'POST',
    }).then(res => window.location.href = "http://localhost:7000/")

}

/*
* Method for redirecting after adding a social event.
 */
function addSocialEventsRedirect() {

    fetch('http://localhost:7000/socialevents?title='+ document.getElementById("title").value, {
        method: 'POST',
    }).then(res => window.location.href = "http://localhost:7000/")

}

