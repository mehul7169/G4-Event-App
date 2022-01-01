/*
* DEBUG MENU FUNCTION
* (deprecated) Method for updating dropdown list of individuals that can check into an event
* (based on which event has been selected from check in form's drop down and which
* users have been invited to that event).
* param selectObj: pointer to the select box/select dropdown of individuals.
 */
function changeIndividualOptionsBasedOnEventCheckIn(selectObj) {
    var idx = selectObj.selectedIndex;
    var event = selectObj[idx].value;
    var individualSelect = document.getElementById("ind");
    var newOption;
    var rawResponseJson;
    while (individualSelect.options.length > 0) {
        individualSelect.remove(0);
    }
    const rawResponse = fetch('http://localhost:7000/findwhosinvited?event=' + event, {
            method: 'Post',
        }
    );
    while (individualSelect.options.length > 0) {
            individualSelect.remove(0);
    }
    const checkedInResponse = fetch('http://localhost:7000/findwhoscheckedin?event=' + event, {
        method: 'Post',
        }
    );
    var checkedIn = Object.keys(checkedInResponse.json()).length;
    if (checkedIn <= event.capacity) {
        rawResponse.then(function (rawResponse) {
            rawResponseJson = rawResponse.json();
            rawResponseJson.then(function (rawResponseJson) {

                for (var i = 0; i < rawResponseJson.length; i++) {
                    newOption = document.createElement("option");
                    newOption.value = rawResponseJson[i]['ID'];
                    newOption.text = rawResponseJson[i]['name'];
                    individualSelect.add(newOption)
                    console.log("TEST:" + newOption.value);

                }
            })
        });
    }
}

/*
* DEBUG MENU FUNCTION
* Method for displaying an alert/notifying a user that they have
* successfully sent an invitation.
 */
function checkInAlert() {
    alert("Thanks for checking in!");
}
