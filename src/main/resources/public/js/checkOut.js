
/* Functions Related to Check-Out*/

/*
* Method for updating dropdown list of individuals that can check out of an event
* (based on which event has been selected from check in form's drop down and which
* users have checked into that event).
* param selectObj: pointer to the select box/select dropdown of individuals.
 */
function changeIndividualOptionsBasedOnEventCheckOut(selectObj) {
    var idx = selectObj.selectedIndex;
    var event = selectObj[idx].value;
    var individualSelect = document.getElementById("indCheckOut");
    var newOption;
    var rawResponseJson;
    while (individualSelect.options.length > 0) {
        individualSelect.remove(0);
    }
    const rawResponse = fetch('http://localhost:7000/findwhoscheckedin?event=' + event, {
            method: 'Post',
        }
    );
    rawResponse.then(function(rawResponse) {
        rawResponseJson = rawResponse.json();
        rawResponseJson.then(function (rawResponseJson) {

            for (var i = 0; i < rawResponseJson.length; i++) {
                newOption = document.createElement("option");
                newOption.value = rawResponseJson[i]['ID'];
                newOption.text = rawResponseJson[i]['name'];
                individualSelect.add(newOption)
            }

        })

    });
    // console.log(rawResponseJson);
    //
    // console.log(rawResponse);
    //     for (var i=0; i < 10; i++) {
    //         newOption = document.createElement("option");
    //         newOption.value = "test";
    //         newOption.text = "test";
    //         individualSelect.add(newOption);
    //     }
}

function checkOutAlert() {
    alert("You've successfully checked out");
}