
/*Functions Related to the Event Feed and Creating Events */

/*
* Function for deleting a selected social event.
* param eventID; the ID of the social event being deleted
 */
function deleteSocialEvent(eventID) {
    //delete from social event table
    fetch('http://localhost:7000/socialevents?ID=' + eventID, {
            method: 'Delete',
        }
    ).then(res => window.location.reload = window.location.reload(true));

    //delete from event table
    const eventTitle = document.getElementById("deleteSocialEvent").value;
    fetch('http://localhost:7000/events?title=' + eventTitle, {
            method: 'Delete',
        }
    ).then(res => window.location.reload = window.location.reload(true));
}

/*
* Function for deleting a selected seminar.
* param eventID; the ID of the seminar being deleted
 */
function deleteSeminar(eventID) {
    //delete from seminar table
    fetch('http://localhost:7000/seminars?ID=' + eventID, {
            method: 'Delete',
        }
    ).then(res => window.location.reload = window.location.reload(true));
    //refresh homepage to see that event was deleted
    //refreshHome();

    //delete from event table
    const eventTitle = document.getElementById("deleteSeminar").value;
    fetch('http://localhost:7000/events?title=' + eventTitle, {
            method: 'Delete',
        }
    ).then(res => window.location.reload = window.location.reload(true));
}

/*
* Function for adding an event (helper that navigates to appropriate add function
* based on the type of event being created.
* Queries doc for event type.
 */
function addEvent() {
    console.log("in add event")
    //figure out type being added and call appropriate method
    const eventType = document.getElementById("eventType");
    let added = false;
    switch(eventType.value) {
        case "seminar":
            added = addSeminar();
            if (added == false) { return false; }
            break;
        case "socialevent":
            added = addSocialEvent();
            if (added == false) { return false; }
            break;
    }
}

/*
* Method for creating a new instance of Seminar.
* Queries doc for necessary params for post request.
 */
function addSeminar() {
    //clump all selected tags into a csv string
    var selected = [];
    for (var option of document.getElementById('eventtags').options)
    {
        if (option.selected) {
            selected.push(option.value);
        }
    }
    let tags = "";
    for (let i = 0; i < selected.length; i++) {
        tags += selected[i];
        tags += ',';
    }
    tags = tags.substring(0, tags.length - 1);

    //query doc for other params and assess validity when necessary
    const title = document.getElementById("title");
    const description = document.getElementById("description");
    const addLine1 = document.getElementById("addLine1");
    const addLine2 = document.getElementById("addLine2");
    const city = document.getElementById("city");
    const speaker = document.getElementById("speaker");
    const state = document.getElementById("state");
    if (!validateState(state)) { return false; }
    const zip = document.getElementById("zip");
    if (!validateZip(zip)) { return false; }
    const priv = document.getElementById("priv");
    const datetime = document.getElementById("datetime");
    const school = document.getElementById("school");
    const field = document.getElementById("field");
    const capacity = document.getElementById("capacity");
    if (!validateCapacity(capacity)) { return false; }
    const group = document.getElementById("group");

    console.log(datetime)
    console.log(datetime.value)
    console.log(group.value);

    //send post request
    fetch('http://localhost:7000/seminars?title=' + title.value +
        '&description=' + description.value + '&tags=' + tags +
         '&speaker=' + speaker.value + '&addLine1=' + addLine1.value +
        '&addLine2=' + addLine2.value + '&city=' + city.value + '&state=' + state.value +
        '&zip=' + zip.value +'&datetime=' + datetime.value +
        '&priv=' + priv.value +'&school=' + school.value + '&field=' + field.value + '&capacity=' + capacity.value +
         '&group=' + group.value, {
            method: 'Post',
        }
    ).then(res => window.location.reload = window.location.reload(true));

    return true;
}

/*
* Method for creating a new instance of Social Event.
* Queries doc for necessary params for post request.
 */
function addSocialEvent() {
    //clump all selected tags into a csv string
    var selected = [];
    for (var option of document.getElementById('eventtags').options)
    {
        if (option.selected) {
            selected.push(option.value);
        }
    }
    let tags = "";
    for (let i = 0; i < selected.length; i++) {
        tags += selected[i];
        tags += ',';
    }
    tags = tags.substring(0, tags.length - 1);

    //query doc for other params and assess validity when necessary
    const title = document.getElementById("title");
    const description = document.getElementById("description");
    const addLine1 = document.getElementById("addLine1");
    const addLine2 = document.getElementById("addLine2");
    const city = document.getElementById("city");
    const capacity = document.getElementById("capacity");
    if (!validateCapacity(capacity)) { return false; }
    const state = document.getElementById("state");
    if (!validateState(state)) { return false; }
    const zip = document.getElementById("zip");
    if (!validateZip(zip)) { return false; }
    const priv = document.getElementById("priv");
    const datetime = document.getElementById("datetime");
    const group = document.getElementById("group");

    console.log(datetime)
    console.log(datetime.value)
    console.log(group.value);

    //send post request
    fetch('http://localhost:7000/socialevents?title=' + title.value +
        '&description=' + description.value + '&tags=' + tags +
        '&capacity=' + capacity.value + '&addLine1=' + addLine1.value +
        '&addLine2=' + addLine2.value + '&city=' + city.value + '&state=' + state.value +
        '&zip=' + zip.value +'&datetime=' + datetime.value +
        '&priv=' + priv.value + '&group=' + group.value, {
            method: 'Post',
        }
    ).then(res => window.location.reload = window.location.reload(true));

    return true;
}

/*
* Method for handling search events (displaying filtered feed).
* Queries doc for necessary params for get request.
 */
function displaySearchResults() {
    const searchString = document.getElementById("searchString");
    fetch('http://localhost:7000/searchbytitleanddescription?searchString=' +
        searchString.value, {
            method: 'Get',
        }
    )
}

/*
* Method for handling filter by tag functionality (displaying filtered feed).
* Queries doc for necessary params for get request.
 */
function displayFilterResults() {
    const filteredTag = document.getElementById("tag");
    fetch('http://localhost:7000/filterbytag?tag=' +
        filteredTag.value, {
            method: 'Get',
        }
    )
}

/*
* Helper method for refreshing the page by redirecting to the homepage.
 */
function refreshHome() {
    fetch('http://localhost:7000/', {
            method: 'Get',
        }
    ).then(res => window.location.reload = window.location.reload(true));
}

/*
* Checks that supplied capacity (for create event form) is valid (cap > 0 ).
* return: true if valid, false otherwise (and displays alert)
 */
function validateCapacity(capacity) {
    if(capacity == null) {
        return true;
    }
    if(capacity.value <= 0 ) {
        alert("Capacity must be at least 1!");
        return false;
    }
    return true;
}

/*
* Checks that supplied state abbreviation(for create event form) is valid.
* return: true if valid, false otherwise (and displays alert)
 */
function validateState(state) {
    let theState = state.value.toUpperCase();
    //remove whitespace on either end
    theState = theState.trim();
    if  (theState == "AL" || theState == "AK" || theState == "AZ" ||
        theState == "AR" || theState == "CA" || theState == "CO" || theState == "CT" ||
        theState == "DE" || theState ==  "FL" || theState == "GA" || theState == "HI" ||
        theState == "ID" || theState == "IL" || theState == "IN" || theState == "IA" ||
        theState == "KS" || theState == "KY" || theState == "LA" || theState ==  "ME" ||
        theState == "MD" || theState == "MA" || theState == "MI" || theState == "MN" ||
        theState == "MS" || theState == "MO" || theState == "MT" || theState == "NE" ||
        theState == "NV" || theState == "NH" || theState == "NJ" || theState == "NM" ||
        theState == "NY" || theState == "NC" || theState == "ND" || theState == "OH" ||
        theState == "OK" || theState == "OR" || theState == "PA" || theState == "RI" ||
        theState == "SC" || theState == "SD" || theState == "TN" || theState == "TX" ||
        theState == "UT" || theState == "VT" || theState == "VA" || theState == "WA" ||
        theState == "WV" || theState == "WI" || theState == "WY" || theState == "AS" ||
        theState == "DC" || theState == "FM" || theState == "GU" || theState == "MH" ||
        theState == "MP" || theState == "PW" || theState == "PR" || theState == "VI" )
    {
        return true;
    }
    else {
        alert("Please enter a valid state abbreviation!");
        return false;
    }
}

/*
* Checks that supplied zip code (for create event form) is valid (4 or 5 digits).
* return: true if valid, false otherwise (and displays alert)
 */
function validateZip(zip) {
    if (/^\d{5}(-\d{4})?$/.test(zip.value)) {
        return true;
    }
    alert("Please enter a valid zip code!");
    return false;
}
