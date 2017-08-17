const DEFAULT_INVESTIGATION_ELEMENT_HEIGHT = 210;
const DEFAULT_EMPLOYEE_ELEMENT_HEIGHT = 17;
const DEFAULT_ROW_IN_TABLE_HEIGHT = 40;

const DEFAULT_INVISIBLE_INVESTIGATION_COUNT_ON_PAGE = 1;
const DEFAULT_EMPLOYEES_ON_PAGE = 8;
const DEFAULT_EMPLOYEES_IN_TABLE = 10;

const DEFAULT_INVISIBLE_EMPLOYEES_COUNT = 3;

const INVESTIGATION_FIELD_TYPE = {
    NUMBER: "number",
    TITLE: "title",
    DESCRIPTION: "description",
    START_DATE: "startDate",
    END_DATE: "endDate"
};


$(function () {

    console.log("onload() function");

    debugger;

    // Prevent all click events on label elements
    // on add/edit investigation modal.
    // document.body.addEventListener("click", function (event) {
    //     if (event.target.className === "modal-label") {
    //         console.error("Prevent even!!!");
    //         event.preventDefault();
    //     }
    // });

    // Object.defineProperty(Object.prototype, "forEach", {
    //     value: function (func) {
    //         for (var key in this) {
    //             if (!this.hasOwnProperty(key)) continue;
    //             var value = this[key];
    //             func(key, value);
    //         }
    //     }, enumerable: false
    // });


    $(window).on("scroll", daHelper.scrollingHeaderEvent("headerContainer"));

    initInvestigationModalWindow();
    daHelper.settingDateTimePickers("start_datetimepicker", "end_datetimepicker", "removeFilterButton", filter);
    initInvestigationsLoading();

    $("#investigationModal").on("hidden.bs.modal", function (e) {
        debugger;
        resetInvestigationModalWindow();
    });

    $("#involvedStaffTable").on("hidden.bs.modal", function (e) {
        resetInvolvedStaffTableModalDialog();
    });
});

function initInvestigationsLoading() {

    console.log("initInvestigationsLoading()");

    debugger;

    let requestParams = {
        offset: 0,
        limit: getCountInvestigationsInPage()
    };

    let successfulResponse2GetInvestigation = new SuccessfulResponse2GetInvestigation("containerForInvestigations", "alerts_area", daHelper.drawInvestigations);

    let investigationLoader = new daHelper.DataLoader("GET", daHelper.INVESTIGATIONS_URL,
        successfulResponse2GetInvestigation,
        daHelper.failureResponseWithDrawEmptyData("containerForInvestigations", "alerts_area",
            daHelper.LOAD_ERROR_MESSAGE),
        requestParams, {Accept: "application/json"}, null, "text json");

    investigationLoader.loadData();
}

function initInvestigationModalWindow() {

    console.log("initInvestigationModalWindow()");


    $("#investigationNumber")[0].CustomValidation = new daHelper.CustomValidation(
        $("#investigationNumber")[0], numberValidityCheck, INVESTIGATION_FIELD_TYPE.NUMBER);
    $("#investigationNumber").on("change", () => $("#investigationNumber")[0].CustomValidation.checkValidity());

    $("#investigationTitle")[0].CustomValidation = new daHelper.CustomValidation(
        $("#investigationTitle")[0], titleValidityCheck, INVESTIGATION_FIELD_TYPE.TITLE);
    $("#investigationTitle").on("change", () => $("#investigationTitle")[0].CustomValidation.checkValidity());

    $("#investigationDescription")[0].CustomValidation = new daHelper.CustomValidation(
        $("#investigationDescription")[0], descriptionValidityCheck, INVESTIGATION_FIELD_TYPE.DESCRIPTION);
    $("#investigationDescription").on("change", () => $("#investigationDescription")[0].CustomValidation.checkValidity());

    $("#investigationStartDate")[0].CustomValidation = new daHelper.CustomValidation(
        $("#investigationStartDate")[0], startDateValidityCheck, INVESTIGATION_FIELD_TYPE.START_DATE);
    $("#investigationStartDate").on("change", () => $("#investigationStartDate")[0].CustomValidation.checkValidity());

    $("#investigationEndDate")[0].CustomValidation = new daHelper.CustomValidation(
        $("#investigationEndDate")[0], endDateValidityCheck, INVESTIGATION_FIELD_TYPE.END_DATE);
    $("#investigationEndDate").on("change", () => $("#investigationEndDate")[0].CustomValidation.checkValidity());

}

function resetInvestigationModalWindow() {

    console.log("resetInvestigationModalWindow()");

    debugger;

    let modalTitleElement = document.getElementById("modal_title");
    modalTitleElement.innerText = "Edit investigation";

    $("#involvedStaff").multiSelect("destroy");
    $("#involvedStaff").empty();

    $("#investigationNumber")[0].CustomValidation.resetValidation();
    $("#investigationTitle")[0].CustomValidation.resetValidation();
    $("#investigationDescription")[0].CustomValidation.resetValidation();
    $("#investigationStartDate")[0].CustomValidation.resetValidation();
    $("#investigationEndDate")[0].CustomValidation.resetValidation();

    $("#investigationNumber").val('');
    $("#investigationTitle").val('');
    $("#investigationDescription").val('');
    $("#investigationStartDate").val('');
    $("#investigationEndDate").val('');
}

function prepareEditInvestigationModalWindow(element) {

    console.log("prepareEditInvestigationModalWindow(element)");

    debugger;

    let modalTitleElement = document.getElementById("modal_title");
    modalTitleElement.innerText = "Edit investigation";

    let investigationElements = element.children;

    let investigationId = investigationElements[0].children[0].id.split(daHelper.INVESTIGATION_ID_PREFIX)[1];
    let number = investigationElements[0].children[1].innerText.split(daHelper.INVESTIGATION_NUMBER_PREFIX)[1];
    let title = investigationElements[0].children[2].innerText;
    let startAndEndDates = investigationElements[1].children[1].innerText.split(daHelper.INVESTIGATION_DATE_SEPARATOR.trim());
    let description = investigationElements[2].firstElementChild.innerText;

    daHelper.settingDateTimePickers("investigationStartDate", "investigationEndDate", null, function () {
    });

    $("#investigationNumber").val(number);
    $("#investigationTitle").val(title);
    $("#investigationDescription").val(description);
    $("#investigationStartDate").val(startAndEndDates[0].trim());

    if (startAndEndDates.length >= 2 && daHelper.isString(startAndEndDates[1])
        && startAndEndDates[1].length !== 0) {
        $("#investigationEndDate").val(startAndEndDates[1].trim());
    }

    $("#investigationNumber")[0].CustomValidation.checkValidity();
    $("#investigationTitle")[0].CustomValidation.checkValidity();
    $("#investigationDescription")[0].CustomValidation.checkValidity();
    $("#investigationStartDate")[0].CustomValidation.checkValidity();
    $("#investigationEndDate")[0].CustomValidation.checkValidity();

    return {
        investigationId: parseInt(investigationId, 10),
        number: number,
        title: title.length > 0 ? title : null,
        description: description,
        startDate: startAndEndDates[0].trim(),
        endDate: startAndEndDates[1].trim().length > 0 ? startAndEndDates[1].trim() : null
    };
}

function setMultiselectElementInModalWindow(successfulFunction, failureFunction) {

    console.log("setMultiselectElementInModalWindow(successfulFunction, failureFunction)");

    debugger;
    $("#involvedStaff").multiSelect({
        selectableHeader: "<div class=\"modal_multiselect_lists-header\">All non involved employees</div>",
        selectionHeader: "<div class=\"modal_multiselect_lists-header\">Involved employees</div>"
    });

    let multiSelectContainer = document.getElementById("ms-" + "involvedStaff");

    let allEmployeesLoader = new daHelper.DataLoader("GET", daHelper.EMPLOYEES_URL,
        successfulFunction(multiSelectContainer.children[0], drawEmployee),
        failureFunction(multiSelectContainer.children[0], "alerts_area"),
        {offset: 0, limit: DEFAULT_EMPLOYEES_ON_PAGE}, {Accept: "application/json"}, null, "text json");

    let selectableContainer = document.getElementById("ms-" + "involvedStaff").children[0].children[1];

    selectableContainer.onscroll = function () {
        setEmployeesScroll(selectableContainer, allEmployeesLoader)
    };

    allEmployeesLoader.loadData();
}

function addInvestigation() {

    console.log("addInvestigation()");

    let modalTitleElement = document.getElementById("modal_title");
    modalTitleElement.innerText = "Add investigation";

    daHelper.settingDateTimePickers("investigationStartDate", "investigationEndDate", null, function () {
    });

    setMultiselectElementInModalWindow(daHelper.successfulResponse2GetAllElementsInMultiselect, daHelper.failureResponseInMultiselect);

    $("#investigationModal").modal("show");
    $("#modal_save").off('click').on('click', function () {

        debugger;
        if (!checkValidation()) return;

        let addInvestigation = getInvestigationFromModalWindow();

        let successfulResponse = function (containerElementId, alertAreaElementId, investigations) {
            return function (result) {

                debugger;
                daHelper.drawMessage(alertAreaElementId, "Data was successful saved.",
                    daHelper.MESSAGE_TYPE.success, daHelper.DEFAULT_ERROR_MESSAGE_ALIVE_TIME_IN_SEC);

                investigations[0].investigationId = result;
                daHelper.drawInvestigations(containerElementId, investigations);
                $("#investigationModal").modal("hide");

                if ($("#containerForEmptyDataMessage").length) $("#containerForEmptyDataMessage").remove();
            }
        };

        debugger;

        let investigationLoader = new daHelper.DataLoader("POST", daHelper.INVESTIGATIONS_URL,
            successfulResponse("containerForInvestigations", "alerts_area", [addInvestigation]),
            daHelper.failureResponse("alerts_area", daHelper.SEND_ERROR_MESSAGE),
            JSON.stringify(addInvestigation), null, "application/json;charset=UTF-8", null);
        investigationLoader.loadData();
    });
}

function editInvestigation(element) {

    console.log("edit investigation with id: " + element.firstElementChild.firstElementChild.id.split(daHelper.INVESTIGATION_ID_PREFIX)[1]);

    debugger;

    let originalInvestigation = prepareEditInvestigationModalWindow(element);

    let lastLoadingInvolvedEmployeeId = -1;
    let lastLoadingEmployeeId = -1;

    let successfulResponse2GetAllEmployeesInMultiselect = function (element, drawElementFunction) {

        return function (result) {
            debugger;
            daHelper.enableLoadingAnimationInMultiselect(element, false);

            if (involvedEmployeesLoader.isAvailableMoreData && lastLoadingInvolvedEmployeeId <= lastLoadingEmployeeId) {
                daHelper.enableLoadingAnimationInMultiselect(document.getElementById("ms-" + "involvedStaff").children[1], true);
                involvedEmployeesLoader.loadData();
            }

            let returnedObjects = daHelper.getArrayOfObjects(result);
            if (returnedObjects.length !== this.requestData.limit) this.isAvailableMoreData = false;

            this.requestData.offset += returnedObjects.length;

            if (returnedObjects.length > 0) {
                lastLoadingEmployeeId = returnedObjects[returnedObjects.length - 1].employeeId;

                for (let employee of returnedObjects) {
                    drawElementFunction(employee);
                }
            }
        }
    };

    let successfulResponse2GetInvolvedStaffInMultiselet = function (element) {

        return function (result) {
            debugger;
            daHelper.enableLoadingAnimationInMultiselect(element, false);

            let returnedObjects = daHelper.getArrayOfObjects(result);
            if (returnedObjects.length !== this.requestData.limit) this.isAvailableMoreData = false;

            this.requestData.offset += returnedObjects.length;

            if (returnedObjects.length > 0) {
                lastLoadingInvolvedEmployeeId = returnedObjects[returnedObjects.length - 1].employeeId;
                drawInvolvedEmployees(returnedObjects);
            }
        }
    };

    setMultiselectElementInModalWindow(successfulResponse2GetAllEmployeesInMultiselect, daHelper.failureResponseInMultiselect);

    let multiSelectContainer = document.getElementById("ms-" + "involvedStaff");

    let involvedEmployeesLoader = new daHelper.DataLoader("GET", daHelper.INVESTIGATION_EMPLOYEES_URL + "/" + originalInvestigation.investigationId,
        successfulResponse2GetInvolvedStaffInMultiselet(multiSelectContainer.children[1]),
        daHelper.failureResponseInMultiselect(multiSelectContainer.children[1], "alerts_area"),
        {offset: 0, limit: DEFAULT_EMPLOYEES_ON_PAGE}, {Accept: "application/json"}, null, "text json");

    let selectionContainer = document.getElementById("ms-" + "involvedStaff").children[1].children[1];

    selectionContainer.onscroll = function () {
        setEmployeesScroll(selectionContainer, involvedEmployeesLoader)
    };

    $("#investigationModal").modal("show");

    $("#modal_save").off('click').on("click", function () {

            debugger;

            let editInvestigation = {};

            let requestParams;
            let url = daHelper.INVESTIGATIONS_URL;

            if (!checkValidation()) return;

            let isRedraw = false;
            if (isSameInvestigation(originalInvestigation)) {
                url += "/" + originalInvestigation.investigationId + daHelper.STAFF_URL;

                requestParams = getSelectedEmployeesIds();
            } else {
                editInvestigation = getInvestigationFromModalWindow();
                editInvestigation.investigationId = originalInvestigation.investigationId;

                requestParams = editInvestigation;
                isRedraw = true;
            }

            let successfulResponse = function (investigation, alertAreaElementId, isRedraw) {
                return function (result) {

                    debugger;
                    daHelper.drawMessage(alertAreaElementId, "Data was successful changed.",
                        daHelper.MESSAGE_TYPE.success, daHelper.DEFAULT_ERROR_MESSAGE_ALIVE_TIME_IN_SEC);

                    if (isRedraw) redrawInvestigation(investigation);
                    $("#investigationModal").modal("hide");
                }
            };

            debugger;

            let investigationLoader = new daHelper.DataLoader("PUT", url,
                successfulResponse(editInvestigation, "alerts_area", isRedraw),
                daHelper.failureResponse("alerts_area", daHelper.SEND_ERROR_MESSAGE),
                JSON.stringify(requestParams), null, "application/json;charset=UTF-8", null);
            investigationLoader.loadData();
        }
    );

}

function removeInvestigation(element) {

    console.log("removeInvestigation(element) - with id: " + element.firstElementChild.firstElementChild.id.split(daHelper.INVESTIGATION_ID_PREFIX)[1]);

    debugger;

    let investigationId = element.firstElementChild.firstElementChild.id.split(daHelper.INVESTIGATION_ID_PREFIX)[1];

    $("#confirmModal").modal("show");

    $("#confirmOk").off('click').on('click', function () {

        $("#confirmModal").addClass("loading");

        let removeDataLoader = new daHelper.DataLoader("DELETE", daHelper.INVESTIGATIONS_URL + "/" + investigationId,
            function (result) {

                element.remove();
                $("#confirmModal").modal("hide").removeClass("loading");
                daHelper.drawMessage("alerts_area", "Investigation removed.",
                    daHelper.MESSAGE_TYPE.success, daHelper.DEFAULT_ERROR_MESSAGE_ALIVE_TIME_IN_SEC);
            },
            function (jqXHR, textStatus, errorThrown) {

                $("#confirmModal").modal("hide").removeClass("loading");
                daHelper.drawMessage("alerts_area", "Can't remove investigation. Please check connection and try again.",
                    daHelper.MESSAGE_TYPE.danger, daHelper.DEFAULT_ERROR_MESSAGE_ALIVE_TIME_IN_SEC);
            }, null, null, null, null);

        removeDataLoader.loadData();
    });

    $("#confirmCancel").off('click').on('click', function () {
        $("#confirmModal").modal("hide");
    });
}

function filter(startDate, endDate) {

    clearInvestigationsHTMLElementsInDOM();

    let requestParams = {
        startDate: startDate,
        endDate: endDate,
        offset: 0,
        limit: getCountInvestigationsInPage()
    };

    let successfulResponse2GetInvestigation = new SuccessfulResponse2GetInvestigation("containerForInvestigations", "alerts_area", daHelper.drawInvestigations);

    let investigationLoader = new daHelper.DataLoader("GET", daHelper.INVESTIGATIONS_FILTER_URL,
        successfulResponse2GetInvestigation,
        daHelper.failureResponseWithDrawEmptyData("containerForInvestigations", "alerts_area",
            daHelper.LOAD_ERROR_MESSAGE),
        requestParams, {Accept: "application/json"}, null, "text json");

    investigationLoader.loadData();
}

function removeFilter() {

    console.log("removeFilter()");

    document.getElementById("start_datetimepicker").value = null;
    document.getElementById("end_datetimepicker").value = null;

    document.getElementById("removeFilterButton").style.display = "none";

    clearInvestigationsHTMLElementsInDOM();
    initInvestigationsLoading();
}

function showInvolvedStaff(investigationElement) {

    console.log("showInvolvedStaff(investigationElement)");

    debugger;

    let investigationId = investigationElement.firstElementChild.firstElementChild.id.split(daHelper.INVESTIGATION_ID_PREFIX)[1];

    let successfulResponseFunction = function () {

        return function (result) {

            debugger;
            enableLoadingAnimationInInvolvedStaffTable(false);

            let returnedObjects = daHelper.getArrayOfObjects(result);
            if (returnedObjects.length !== this.requestData.limit) this.isAvailableMoreData = false;

            this.requestData.offset += returnedObjects.length;

            if (returnedObjects.length > 0) {
                drawInvolvedStaffInTable(returnedObjects);

            } else if (this.requestData.offset === 0) {
                drawEmptyDataMessageInTable("No data.");
            }

            let element = document.getElementById("involvedStaffTableModalBody");
            element.onscroll = function () {
                setEmployeesScrollInTable(involvedStaffLoader);
            };
        }
    };

    let failureRequestFunction = function (alertAreaElementId) {

        return function (jqXHR, textStatus, errorThrown) {
            debugger;

            enableLoadingAnimationInInvolvedStaffTable(false);

            drawEmptyDataMessageInTable("Data is not available.");
            daHelper.drawMessage(alertAreaElementId, daHelper.LOAD_ERROR_MESSAGE,
                daHelper.MESSAGE_TYPE.danger, daHelper.DEFAULT_ERROR_MESSAGE_ALIVE_TIME_IN_SEC);
        }
    };

    let involvedStaffLoader = new daHelper.DataLoader("GET", daHelper.INVESTIGATION_EMPLOYEES_URL + "/" + investigationId,
        successfulResponseFunction(), failureRequestFunction("alerts_area"),
        {offset: 0, limit: DEFAULT_EMPLOYEES_IN_TABLE}, {Accept: "application/json"}, null, "text json");

    $("#involvedStaffTable").modal("show");
    involvedStaffLoader.loadData();
}

function setEmployeesScrollInTable(loader) {

    console.log("setInvestigationsScrollInTable(loader)");

    debugger;

    if (!loader.isAvailableMoreData) return;

    let element = document.getElementById("involvedStaffTableModalBody");

    let scrollHeight = element.scrollHeight;
    let scrollTop = element.scrollTop;
    let clientHeight = element.clientHeight;

    if (scrollHeight - scrollTop - clientHeight < DEFAULT_ROW_IN_TABLE_HEIGHT * DEFAULT_INVISIBLE_EMPLOYEES_COUNT) {

        element.onscroll = null;
        enableLoadingAnimationInInvolvedStaffTable(true);
        loader.loadData();
    }
}

function drawEmptyDataMessageInTable(message) {

    console.log("drawEmptyDataMessageInTable(message)");

    let container = document.createElement("div");
    container.id = "containerForEmptyDataMessageInTable";
    container.setAttribute("style", "text-align: center; margin: 10px auto 10px;");

    document.getElementById("involvedStaffTableModalBody").appendChild(container);

    let emptyMessageElement = document.createElement("h4");
    emptyMessageElement.setAttribute("style", "color: black; font-style: italic;");
    emptyMessageElement.innerText = message;
    container.appendChild(emptyMessageElement);
}

function drawInvolvedStaffInTable(employees) {

    console.log("drawInvolvedStaffInTable(employees)");

    if (!(employees instanceof Array)) return;

    for (let i = 0; i < employees.length; i++) {
        let tableBodyElement = document.getElementById("involvedStaffTableBody");
        tableBodyElement.appendChild(createTableRowElements(employees[i]));
    }
}

function createTableRowElements(employee) {

    console.log("createTableRowElements(employee)");

    debugger;

    let dateFormat = daHelper.DATE_TIME_FORMAT.split(" ")[0];

    let ageDate = null;
    let age = moment(employee.age);

    if (age.isValid()) {
        age = moment().diff(age, "years");
        ageDate = moment(employee.age).format(dateFormat);
    }
    else {
        age = ageDate = "N/A";
    }

    let month = "N/A";
    let year = "N/A";

    let startWorkingDate = moment(employee.startWorkingDate);

    if (startWorkingDate.isValid()) {
        year = moment().diff(startWorkingDate, "years");
        month = moment().diff(startWorkingDate, "months") % 12;
        startWorkingDate = startWorkingDate.format(dateFormat);
    }

    let tableRowElement = document.createElement("tr");
    let createdRowsHTML = "<td>" + employee.name + "</td>" +
        "<td title=\"" + ageDate + "\">" + age + "</td>" +
        "<td title=\"" + startWorkingDate + "\">" + year + daHelper.EMPLOYEE_YEAR_MONTH_SEPARATOR + month + "</td>";

    tableRowElement.insertAdjacentHTML("beforeend", createdRowsHTML);
    return tableRowElement;
}

function resetInvolvedStaffTableModalDialog() {

    console.log("resetInvolvedStaffTableModalDialog()");
    debugger;
    $("#involvedStaffTableBody").empty();
    $("#containerForEmptyDataMessageInTable").remove();
}

function enableLoadingAnimationInInvolvedStaffTable(state) {

    console.log("enableLoadingAnimationInParticipatedInvestigationsTable(state)");

    let elementDisplayMode = "none";
    if (state) elementDisplayMode = "block";

    $("#modalTableLoadingAnimation").css("display", elementDisplayMode);
}

function drawInvolvedEmployees(employees) {

    console.log("drawInvolvedEmployees(employees)");

    if (employees == null) employees = [];
    if (employees.constructor !== Array) employees = [employees];

    for (let employee of employees) {
        if (!isEmployeeDraw(employee)) {
            drawEmployee(employee);
        }
        setEmployeeSelected(employee);
    }
}

function drawEmployee(employee) {

    console.log("drawEmployee(employee)");

    debugger;

    if (isEmployeeDraw(employee)) return;

    let employeesContainerElement = document.getElementById("involvedStaff");
    let optionElement = document.createElement("option");
    optionElement.setAttribute("value", employee.employeeId);
    optionElement.innerText = employee.name;

    employeesContainerElement.appendChild(optionElement);
    let arrayElementsPostfix = ["-selectable", "-selection"];

    // id with postfix '-selectable' and '-selection'
    let selectableListContainerElement = document.getElementById("ms-" + "involvedStaff").children[0];
    let selectionListContainerElement = document.getElementById("ms-" + "involvedStaff").children[1];

    let notInvolvedEmployeeElement = createEmployeeLiElement(employee, arrayElementsPostfix[0], true);
    let involvedEmployeeElement = createEmployeeLiElement(employee, arrayElementsPostfix[1], false);

    selectableListContainerElement.children[1].appendChild(notInvolvedEmployeeElement);
    selectionListContainerElement.children[1].appendChild(involvedEmployeeElement);

    $("#involvedStaff").multiSelect("addExistsElementsByIdOrValue", "id=" + employee.employeeId);
}

function createEmployeeLiElement(employee, classPostfix, isVisible) {

    console.log("createEmployeeLiElement(employee, classPostfix)");

    debugger;

    let liElement = document.createElement("li");
    liElement.className = "ms-elem" + classPostfix;

    liElement.id = "" + employee.employeeId + classPostfix;
    liElement.style.display = isVisible ? "list-item" : "none";
    liElement.innerHTML = "<span>" + employee.name + "</span>";

    return liElement;
}

function isEmployeeDraw(employee) {

    console.log("isEmployeeDraw(employee)");

    debugger;
    let allEmployeesElements = document.getElementById("involvedStaff").children;

    let isDraw = false;
    for (let i = 0; i < allEmployeesElements.length; i++) {
        if (parseInt(allEmployeesElements[i].value, 10) === employee.employeeId) {
            isDraw = true;
            break;
        }
    }
    return isDraw;
}

function setEmployeeSelected(employee) {

    console.log("setEmployeeSelected(employee)");

    let selectableElement = document.getElementById(employee.employeeId + "-selectable");
    selectableElement.style.display = "none";
    selectableElement.className += " ms-selected";

    let selectionElement = document.getElementById(employee.employeeId + "-selection");
    selectionElement.style.display = "list-item";
    selectionElement.className += " ms-selected";
}

function getSelectedEmployeesIds() {

    console.log("getSelectedEmployeesIds()");

    debugger;

    let selectedElementsIds = [];
    let selectedStaffContainerElement = document.getElementById("ms-" + "involvedStaff").children[1].children[1];
    let selectedElements = selectedStaffContainerElement.children;

    for (let i = 0; i < selectedElements.length; i++) {
        if (daHelper.hasClass(selectedElements[i], "ms-selected")) {
            debugger;
            selectedElementsIds.push(selectedElements[i].id.split("-selection")[0]);
        }
    }
    return selectedElementsIds;
}

function getInvestigationFromModalWindow() {

    console.log("getInvestigationFromModalWindow()");

    debugger;

    let number = document.getElementById("investigationNumber").value;
    let title = document.getElementById("investigationTitle").value;

    if (!title.length > 0) title = null;

    let startDateElementValue = document.getElementById("investigationStartDate").value;
    let startDate = moment(startDateElementValue, daHelper.DATE_TIME_FORMAT).utc().format();

    let endDateElementValue = document.getElementById("investigationEndDate").value;
    let endDate = moment(endDateElementValue, daHelper.DATE_TIME_FORMAT);

    if (endDate.isValid()) {
        endDate = endDate.utc().format();
    } else {
        endDate = null;
    }

    let description = document.getElementById("investigationDescription").value;

    let involvedStaff = [];
    let employeesIds = getSelectedEmployeesIds();

    for (let i = 0; i < employeesIds.length; i++) {
        involvedStaff.push({
            employeeId: parseInt(employeesIds[i], 10),
            name: daHelper.TEMPLATE_EMPLOYEE_NAME,
            age: daHelper.TEMPLATE_EMPLOYEE_AGE,
            startWorkingDate: daHelper.TEMPLATE_EMPLOYEE_START_WORKING_DATE
        });
    }

    return {
        investigationId: null,
        number: parseInt(number, 10),
        title: title,
        startInvestigationDate: startDate,
        endInvestigationDate: endDate,
        description: description,
        involvedStaff: involvedStaff
    };
}

function isSameInvestigation(investigation) {

    console.log("isSameInvestigation(investigation)");

    debugger;

    let newNumber = document.getElementById("investigationNumber").value;
    let newTitle = document.getElementById("investigationTitle").value;
    let newStartDate = document.getElementById("investigationStartDate").value;
    let newEndDate = document.getElementById("investigationEndDate").value;
    let newDescription = document.getElementById("investigationDescription").value;

    if (investigation.number === newNumber && investigation.title === newTitle
        && investigation.startDate === newStartDate && investigation.endDate === newEndDate
        && investigation.description === newDescription) {
        return true;
    }
    return false;
}

function checkValidation() {

    console.log("checkValidation()");

    debugger;

    let arrayOfCheckedElements = [$("#investigationNumber")[0], $("#investigationTitle")[0],
        $("#investigationDescription")[0], $("#investigationStartDate")[0], $("#investigationEndDate")[0]];

    for (let element of arrayOfCheckedElements) {

        if (!isSuccessfulValidationState(element)) {
            element.focus();
            return false;
        }
    }

    return true;
}

function hasElementValidationState(element) {

    console.log("hasElementValidationState(element)");

    debugger;

    if (daHelper.hasClass(element, "modal_input-validation")
        && element.nextElementSibling !== null && element.nextElementSibling.tagName === "SPAN"
        && daHelper.hasClass(element.nextElementSibling, "modal_input_validation-status_icon")
        && element.parentElement.tagName === "DIV" && daHelper.hasClass(element.parentElement, "modal_validate-container")) {
        return true;
    }
    return false;
}

function isSuccessfulValidationState(element) {

    console.log("isElementValidationSuccessful(element)");

    if (!hasElementValidationState(element)) {
        element.CustomValidation.checkValidity();
    }

    return daHelper.hasClass(element.parentElement, daHelper.arrayValidationSuccessPref[0]);
}

function removePickerData(removeButtonElement) {

    console.log("removePickerData(fieldType, removeButtonElement)");

    let element = removeButtonElement.nextElementSibling;

    if (element.tagName === "DIV") {
        element = element.firstElementChild;
    }

    element.value = "";

    if (element.CustomValidation.fieldType === INVESTIGATION_FIELD_TYPE.END_DATE) {
        element.CustomValidation.checkValidity();
    }
    else {
        element.CustomValidation.resetValidation();
    }
}

function enableLoadingAnimation(state) {

    console.log("enableLoadingAnimation(state)");

    let elementDisplayMode = "none";
    if (state) elementDisplayMode = "block";

    $("#loading_animation").css("display", elementDisplayMode);
}

function getCountInvestigationsInPage() {

    console.log("getCountInvestigationsInPage()");

    let clientHeight = document.documentElement.clientHeight;

    let headerElement = document.getElementsByTagName("header").item(0);
    let footerElement = document.getElementsByTagName("footer").item(0);
    let titleElement = document.getElementsByTagName("main").item(0).firstElementChild.firstElementChild;

    let footerElementHeight = daHelper.getElementHeight(footerElement);
    let investigationAreaHeight = clientHeight - daHelper.getElementHeight(headerElement) - daHelper.getElementHeight(titleElement) - footerElementHeight;
    let maxInvestigationCount = Math.ceil(investigationAreaHeight / DEFAULT_INVESTIGATION_ELEMENT_HEIGHT);

    return maxInvestigationCount + DEFAULT_INVISIBLE_INVESTIGATION_COUNT_ON_PAGE;
}

function clearInvestigationsHTMLElementsInDOM() {

    console.log("clearInvestigationHTMLElementsInDOM()");

    document.getElementsByClassName("investigation").remove();
    if ($("#containerForEmptyDataMessage").length) $("#containerForEmptyDataMessage").remove();
}


function setInvestigationsScroll(loader) {
    console.log("setInvestigationsScroll(loader)");

    return function () {
        debugger;

        if (!loader.isAvailableMoreData) return;

        let scrollHeight = Math.max(
            document.body.scrollHeight, document.documentElement.scrollHeight,
            document.body.offsetHeight, document.documentElement.offsetHeight,
            document.body.clientHeight, document.documentElement.clientHeight
        );

        let scrollTop = window.pageYOffset || document.documentElement.scrollTop;
        let clientHeight = document.documentElement.clientHeight;

        let footerHeight = daHelper.getElementHeight(document.getElementsByTagName("footer").item(0));

        if ((scrollHeight - scrollTop - clientHeight) < DEFAULT_INVESTIGATION_ELEMENT_HEIGHT + footerHeight) {
            daHelper.enableLoadingAnimation(true);
            loader.loadData();
            $(window).off("scroll");
        }
    };
}

function setEmployeesScroll(element, loader) {

    console.log("setEmployeesScroll(element, loader)");

    debugger;

    if (!loader.isAvailableMoreData) return;

    let scrollHeight = element.scrollHeight;
    let scrollTop = element.scrollTop;
    let clientHeight = element.clientHeight;

    if (scrollHeight - scrollTop - clientHeight < DEFAULT_EMPLOYEE_ELEMENT_HEIGHT * DEFAULT_INVISIBLE_EMPLOYEES_COUNT) {

        daHelper.enableLoadingAnimationInMultiselect(element.parentElement, true);
        loader.loadData();
    }
}

function redrawInvestigation(investigation) {

    console.log("redrawInvestigation(investigation)");

    debugger;

    let investigationIdElement = document.getElementById(daHelper.INVESTIGATION_ID_PREFIX + investigation.investigationId);

    investigationIdElement.nextElementSibling.innerText = daHelper.INVESTIGATION_NUMBER_PREFIX + investigation.number;
    investigationIdElement.nextElementSibling.nextElementSibling.innerText = investigation.title == null ? '' : investigation.title;

    let parentElement = investigationIdElement.parentElement;

    let startDate = moment(investigation.startInvestigationDate).format(daHelper.DATE_TIME_FORMAT);
    let endDate = moment(investigation.endInvestigationDate);
    endDate = endDate.parsingFlags().nullInput ? "" : endDate.format(daHelper.DATE_TIME_FORMAT);

    parentElement.nextElementSibling.children[1].innerText = startDate + daHelper.INVESTIGATION_DATE_SEPARATOR + endDate;
    parentElement.nextElementSibling.nextElementSibling.firstElementChild.innerText = investigation.description;
}

let SuccessfulResponse2GetInvestigation = function (dataAreaElementId, alertAreaElementId, drawDataFunction) {
    return function (result) {

        debugger;
        enableLoadingAnimation(false);

        let returnedObjects = daHelper.getArrayOfObjects(result);
        if (returnedObjects.length != this.requestData.limit) this.isAvailableMoreData = false;

        this.requestData.offset += returnedObjects.length;

        if (this.requestData.offset === 0) {

            if (!daHelper.checkOldEmptyDataMessages(dataAreaElementId, daHelper.DEFAULT_NO_DATA_MESSAGE)) {
                daHelper.drawEmptyData(dataAreaElementId, daHelper.DEFAULT_NO_DATA_MESSAGE);
            }
        } else {
            drawDataFunction(dataAreaElementId, returnedObjects);
        }

        $(window).on("scroll", setInvestigationsScroll(this));
        $(window).on("scroll", daHelper.scrollingHeaderEvent("headerContainer"));

    }
};

function setElementValidation(element, fieldType, isValid, message) {

    console.log("setElementValidation(element, fieldType, isValid, message)");

    debugger;

    if (hasElementValidationState(element)) return;

    let arrayPreferences = isValid ? daHelper.arrayValidationSuccessPref : daHelper.arrayValidationErrorPref;

    let validationElementContainer = document.createElement("div");

    if (fieldType === INVESTIGATION_FIELD_TYPE.START_DATE || fieldType === INVESTIGATION_FIELD_TYPE.END_DATE) {
        validationElementContainer.className = "modal_input-datetimepicker";
    } else {
        validationElementContainer.className = "modal-input";
    }

    validationElementContainer.className += " modal_validate-container " + arrayPreferences[0];

    let elementOldParent = element.parentElement;

    element.setAttribute("aria-describedby", element.id + arrayPreferences[1]);
    element.className = "form-control modal_input-validation";

    let elementsHTML = "<span class=\"glyphicon " + arrayPreferences[2] + " modal_input_validation-status_icon\" " +
        "aria-hidden=\"true\"></span>" +
        "<span id=\"" + element.id + arrayPreferences[1] + "\" class=\"sr-only\">" + arrayPreferences[3] + "</span>";

    validationElementContainer.appendChild(element);
    validationElementContainer.insertAdjacentHTML("beforeend", elementsHTML);

    if (typeof message !== typeof undefined || message != null) {
        let errorMessageElement = document.createElement("p");
        errorMessageElement.className = "has-error";
        errorMessageElement.style.textAlign = "justify";
        errorMessageElement.style.marginBottom = 0;
        errorMessageElement.style.fontSize = "14px";
        errorMessageElement.innerText = message;

        validationElementContainer.appendChild(errorMessageElement);
    }

    elementOldParent.appendChild(validationElementContainer);
}


function resetElementValidation(inputElement, fieldType) {

    console.log("resetValidation(inputElement, fieldType)");

    debugger;

    if (!hasElementValidationState(inputElement)) return;

    let oldParentElement = inputElement.parentElement.parentElement;
    inputElement.parentElement.remove();

    inputElement.className = "form-control";

    if (fieldType === INVESTIGATION_FIELD_TYPE.START_DATE || fieldType === INVESTIGATION_FIELD_TYPE.END_DATE) {
        inputElement.className += " modal_input-datetimepicker";
    } else {
        inputElement.className += " modal-input";
    }

    inputElement.removeAttribute("aria-describedby");
    oldParentElement.appendChild(inputElement);
}


let numberValidityCheck = {
    isInvalid: (input) => {

        console.log("numberValidityChecks - inInvalid(input)");
        debugger;
        return !(input.value === null || (input.value !== null && daHelper.isNumber(input.value) && input.value > 0));
    },
    invalidityMessage: "Number can't be empty or less than 0."
};

let titleValidityCheck = {
    isInvalid: (input) => {

        console.log("titleValidityChecks - inInvalid(input)");
        debugger;
        return false;
    },
    invalidityMessage: ""
};

let descriptionValidityCheck = {
    isInvalid: (input) => {

        console.log("descriptionValidityChecks - inInvalid(input)");
        debugger;
        return !(input.value !== null && input.value.trim() !== "");
    },
    invalidityMessage: "Description can't be empty."
};

let startDateValidityCheck = {
    isInvalid: (input) => {

        console.log("startDateValidityChecks - inInvalid(input)");
        debugger;
        let startDateMoment = moment(input.value, daHelper.DATE_TIME_FORMAT);
        return !(startDateMoment.isValid() && startDateMoment.isBefore(moment()));
    },
    invalidityMessage: "Start investigation date can't be null and must be early than now."
};

let endDateValidityCheck = {
    isInvalid: (input) => {

        console.log("endDateValidityChecks - inInvalid(input)");
        debugger;
        let endDateMoment = moment(input.value, daHelper.DATE_TIME_FORMAT);
        let startMoment = moment(document.getElementById("investigationStartDate").value, daHelper.DATE_TIME_FORMAT);

        return !(typeof input.value === "string" && input.value === "" || (endDateMoment.isValid() && endDateMoment.isBefore(moment()) &&
        startMoment.isValid() && startMoment.isBefore(endDateMoment)))
    },
    invalidityMessage: "End investigation date can be null or must be early than start investigation date."
};