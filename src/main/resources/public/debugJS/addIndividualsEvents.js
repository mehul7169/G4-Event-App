
/*
* DEBUG MENU FUNCTION
* (deprecated) Method for adding an individual based on filled form.
* Queries document for necessary params and sends individual post request
 */
function setTagsAndAddIndiv() {

    //clump all selected tags into a csv string
    let select = document.getElementsByTagName('select')[0];
    let result = [];
    let options = select && select.options;
    let opt;

    for (let i = 0, iLen = options.length; i < iLen; i++) {
        opt = options[i];
        if (opt.selected) {
            result.push(opt.text);
        }
    }

    let tags = "";
    for (let i = 0; i < result.length; i++) {
        tags += result[i];
        tags += ',';
    }

    //send post request
    const name = document.getElementById("name");
    const email = document.getElementById("email");
    fetch('http://localhost:7000/individuals?name=' + name.value +
        '&email=' + email.value + '&tags=' + tags, {
            method: 'Post',
        }
    )
}

/*
* DEBUG MENU FUNCTION
* Method for deleting an individual.
* Queries doc for necessary information for sending delete request.
 */
function deleteCurrIndivSetTagsAndAddIndiv() {

    //delete from Individuals table
    const indivName = document.getElementById("name").value;
    fetch('http://localhost:7000/events?title=' + indivName, {
            method: 'Delete',
        }).then(res => window.location.reload = window.location.reload(true));
}


/*
* DEBUG MENU FUNCTION
* Method for creating a new seminar event.
* Queries doc for necessary information for sending post request.
 */
function setTagsAndAddSeminar() {
    //clump all selected tags into a csv string
    let select = document.getElementsByTagName('select')[2];
    let result = [];
    let options = select && select.options;
    let opt;
    for (let i = 0, iLen = options.length; i < iLen; i++) {
        opt = options[i];
        if (opt.selected) {
            result.push(opt.text);
        }
    }
    let tags = "";
    for (let i = 0; i < result.length; i++) {
        tags += result[i];
        tags += ',';
    }

    //query doc for necessary params
    const title = document.getElementById("title");
    const description = document.getElementById("description");
    const addLine1 = document.getElementById("addLine1");
    const addLine2 = document.getElementById("addLine2");
    const city = document.getElementById("city");
    const speaker = document.getElementById("speaker");
    const organization = document.getElementById("organization");
    const state = document.getElementById("state");
    const zip = document.getElementById("zip");
    const priv = document.getElementById("priv");
    const datetime = document.getElementById("datetime");
    const individual = document.getElementById("individual");
    const school = document.getElementById("school");
    const field = document.getElementById("field");

    //send post request
    fetch('http://localhost:7000/seminars?title=' + title.value +
        '&description=' + description.value + '&tags=' + tags +
        '&organization=' + organization.value + '&speaker=' + speaker.value + '&addLine1=' + addLine1.value +
        '&addLine2=' + addLine2.value + '&city=' + city.value + '&state=' + state.value +
         '&zip=' + zip.value +'&datetime=' + datetime.value +'&individual=' + individual.value +
         '&priv=' + priv +'&school=' + school.value + '&field=' + field.value, {
            method: 'Post',
        }
    )
}

/*
* DEBUG MENU FUNCTION
* Method for creating a new social event.
* Queries doc for necessary information for sending post request.
 */
function setTagsAndAddSocialEvent() {

    //clump all selected tags into a csv string
    let select = document.getElementsByTagName('select')[2];
    let result = [];
    let options = select && select.options;
    let opt;
    for (let i = 0, iLen = options.length; i < iLen; i++) {
        opt = options[i];
        if (opt.selected) {
            result.push(opt.text);
        }
    }
    let tags = "";
    for (let i = 0; i < result.length; i++) {
        tags += result[i];
        tags += ',';
    }

    //query doc for necessary params
    const title = document.getElementById("title");
    const description = document.getElementById("description");
    const addLine1 = document.getElementById("addLine1");
    const addLine2 = document.getElementById("addLine2");
    const city = document.getElementById("city");
    const capacity = document.getElementById("capacity");
    const organization = document.getElementById("organization");
    const state = document.getElementById("state");
    const zip = document.getElementById("zip");
    const priv = document.getElementById("priv");
    const datetime = document.getElementById("datetime");
    const individual = document.getElementById("individual");

    //send post request
    fetch('http://localhost:7000/socialevents?title=' + title.value +
        '&description=' + description.value + '&tags=' + tags +
        '&organization=' + organization.value + '&capacity=' + capacity.value + '&addLine1=' + addLine1.value +
        '&addLine2=' + addLine2.value + '&city=' + city.value + '&state=' + state.value +
        '&zip=' + zip.value +'&datetime=' + datetime.value +'&individual=' + individual.value +
        '&priv=' + priv, {
            method: 'Post',
        }
    )
}
