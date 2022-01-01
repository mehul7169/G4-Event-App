
/* Functions Relate to Groups and Memberships */

/*
* Method for adding an individual to a group.
* Queries document for group ID and individual ID and sends membership post request.
 */
function addMember() {
    //get the ID of the individual being added
    let selectedInd = document.getElementById("individual").value

    //get the ID of the group being added
    let selectedGroup = document.getElementById("groupAdd").value;

    //send post request
    fetch('http://localhost:7000/memberships?group=' + selectedGroup +
        '&individual=' + selectedInd, {
            method: 'Post',
        }
    ).then(res => window.location.reload = window.location.reload(true));

}

/*
* Method for deleting an instance of group.
* param groupID: the ID of the group being deleted.
 */
function deleteGroup(groupID) {
    fetch('http://localhost:7000/groups?ID=' + groupID, {
            method: 'Delete',
        }
    ).then(res => window.location.reload = window.location.reload(true));
}

/*
* Method for creating a new instance of Group .
* Queries doc for necessary params for post request.
 */
function addGroup() {
    //get params form doc and check validity
    const name = document.getElementById("groupName");
    if (!validateGroupName(name.value)) { return false; }

    //send post request
    fetch('http://localhost:7000/groups?name=' + name.value,
            {
                method: 'Post',
            }
        ).then(res => window.location.reload = window.location.reload(true));
    window.location.reload = window.location.reload(true)
}

/*
* Checks that supplied email (for create group form) is valid (contains only letters).
* return: true if valid, false otherwise (and displays alert)
 */
function validateGroupName(name) {
    const re = /^[\w\d\s]+$/
    if(re.test(name)) {
        return true;
    }
    alert("Group names can only contain letters, numbers, and spaces!");
    return false;
}

