/*
 * In this page tried use
 *
 * */

const INVESTIGATION_NUMBER_PREFIX = 'N ';
const DEFAULT_INVISIBLE_EMPLOYEE_COUNT_ON_PAGE = 1;
const DEFAULT_EMPLOYEE_ELEMENT_HEIGHT = 238;
const DEFAULT_INVESTIGATION_ELEMENT_HEIGHT = 17;
const DEFAULT_ROW_IN_TABLE_HEIGHT = 40;

const DEFAULT_INVESTIGATIONS_ON_PAGE = 8;
const DEFAULT_INVISIBLE_INVESTIGATIONS_COUNT = 3;
const DEFAULT_INVESTIGATIONS_IN_TABLE = 10;

const EMPLOYEE_FIELD_TYPE = {
    NAME: "name",
    AGE_DATE: "age",
    START_WORKING_DATE: "start_working_date"
};

$(() => {

    console.log("onload() function");

    debugger;

    initEmployeeModalWindow();
    initEmployeesLoading();
    $("#employeeModal").on("hidden.bs.modal", (e) => {
        resetEmployeeModalWindow();
    });

    $("#participatedInvestigationsTable").on("hidden.bs.modal", (e) => {
        resetParticipatedInvestigationsTableModalDialog();
    });

});

function resetEmployeeModalWindow() {

    console.log("resetInvestigationModalWindow()");

    debugger;

    let modalTitleElement = document.getElementById("modal_title");
    modalTitleElement.innerText = "Edit employee";

    $("#investigations").multiSelect("destroy");
    $("#investigations").empty();

    $("#employeeName")[0].CustomValidation.resetValidation();
    $("#employeeAgeDate")[0].CustomValidation.resetValidation();
    $("#employeeWorkingDate")[0].CustomValidation.resetValidation();

    $("#employeeName").val('');
    $("#employeeAgeDate").val('');
    $("#employeeWorkingDate").val('');
}

function setMultiselectElementInModalWindow(successfulFunction, failureFunction) {

    console.log("setMultiselectElementInModalWindow(successfulFunction, failureFunction)");

    debugger;
    $("#investigations").multiSelect({
        selectableHeader: "<div class=\"modal_multiselect_lists-header\">All not participated investigations</div>",
        selectionHeader: "<div class=\"modal_multiselect_lists-header\">Participated investigations</div>"
    });

    let multiSelectContainer = document.getElementById("ms-" + "investigations");

    let allEmployeesLoader = new daHelper.DataLoader("GET", daHelper.INVESTIGATIONS_URL,
        successfulFunction(multiSelectContainer.children[0]), failureFunction(multiSelectContainer.children[0], "alerts_area"),
        {offset: 0, limit: DEFAULT_INVESTIGATIONS_ON_PAGE}, {Accept: "application/json"}, null, "text json");

    let selectableContainer = document.getElementById("ms-" + "investigations").children[0].children[1];

    selectableContainer.onscroll = () => {
        setInvestigationsScroll(selectableContainer, allEmployeesLoader)
    };

    allEmployeesLoader.loadData();
}

function addEmployee() {

    console.log("addEmployee()");

    debugger;

    $("#modal_title").text("Add employee");

    setMultiselectElementInModalWindow(allInvestigationsSuccessfulResponse, investigationsFailureResponse);

    $("#employeeModal").modal("show");

    $("#modal_save").off('click').on('click', () => {

        debugger;
        if (!checkValidation()) return;

        let addEmployee = getEmployeeFromModalWindow();

        let successfulRequestFunction = (containerElementId, alertAreaElementId, employee) => {
            return (result) => {
                debugger;
                daHelper.drawMessage(alertAreaElementId, "Data was successful saved.",
                    daHelper.MESSAGE_TYPE.success, daHelper.DEFAULT_ERROR_MESSAGE_ALIVE_TIME_IN_SEC);

                employee.employeeId = result;
                daHelper.drawEmployees(containerElementId, [employee]);
                $("#employeeModal").modal("hide");

                let rating = getRating(employee.employeeId);
                $("#" + daHelper.EMPLOYEE_ID_PREFIX + employee.employeeId).parent()
                    .find(".list_item-rating_container").children().eq(1).text(rating);
            }
        };

        debugger;
        let dataS = JSON.stringify(addEmployee);

        let employeeLoader = new daHelper.DataLoader("POST", daHelper.EMPLOYEES_URL,
            successfulRequestFunction("containerForEmployees", "alerts_area", addEmployee), failureRequestFunction("alerts_area"),
            JSON.stringify(addEmployee), null, "application/json;charset=UTF-8", null);
        employeeLoader.loadData();
    });

}

function editEmployee(element) {

    console.log("editEmployee(element)");

    let originalEmployee = prepareEditEmployeeModalWindow(element);

    let lastLoadingInvestigationId = -1;
    let lastLoadingParticipatedInvestigationId = -1;

    let allInvestigationsSuccessfulResponse = function (element) {

        return function (result) {
            debugger;
            daHelper.enableLoadingAnimationInMultiselect(element, false);

            if (participatedInvestigationsLoader.isAvailableMoreData && lastLoadingParticipatedInvestigationId <= lastLoadingInvestigationId) {
                daHelper.enableLoadingAnimationInMultiselect(document.getElementById("ms-" + "investigations").children[1], true);
                participatedInvestigationsLoader.loadData();
            }

            let returnedObjects = daHelper.getArrayOfObjects(result);
            if (returnedObjects.length !== this.requestData.limit) this.isAvailableMoreData = false;

            this.requestData.offset += returnedObjects.length;

            if (returnedObjects.length > 0) {
                lastLoadingInvestigationId = returnedObjects[returnedObjects.length - 1].employeeId;

                for (let investigation of returnedObjects) {
                    drawInvestigation(investigation);
                }
            }
        }
    };

    let participatedInvestigationsSuccessfulResponse = function (element) {

        return function (result) {
            debugger;
            daHelper.enableLoadingAnimationInMultiselect(element, false);

            let returnedObjects = daHelper.getArrayOfObjects(result);
            if (returnedObjects.length !== this.requestData.limit) this.isAvailableMoreData = false;

            this.requestData.offset += returnedObjects.length;

            if (returnedObjects.length > 0) {
                lastLoadingParticipatedInvestigationId = returnedObjects[returnedObjects.length - 1].employeeId;
                drawParticipatedInvestigations(returnedObjects);
            }
        }
    };

    setMultiselectElementInModalWindow(allInvestigationsSuccessfulResponse, investigationsFailureResponse);

    let multiSelectContainer = document.getElementById("ms-" + "investigations");

    participatedInvestigationsLoader = new daHelper.DataLoader("GET", daHelper.EMPLOYEE_INVESTIGATIONS_URL + "/" + originalEmployee.employeeId,
        participatedInvestigationsSuccessfulResponse(multiSelectContainer.children[1]), investigationsFailureResponse(multiSelectContainer.children[1], "alerts_area"),
        {offset: 0, limit: DEFAULT_INVESTIGATIONS_ON_PAGE}, {Accept: "application/json"}, null, "text json");

    let selectionContainer = document.getElementById("ms-" + "investigations").children[1].children[1];

    selectionContainer.onscroll = () => {
        setInvestigationsScroll(selectionContainer, participatedInvestigationsLoader);
    };

    $("#employeeModal").modal("show");

    $("#modal_save").off('click').on('click', () => {

        let editEmployee = null;

        let requestParams;
        let url = daHelper.EMPLOYEES_URL;

        if (!checkValidation()) return;

        let isRedraw = false;

        if (isSameEmployee(originalEmployee)) {
            url += "/" + originalEmployee.employeeId + daHelper.INVESTIGATIONS_URL;
            // [id1, id2,id3]
            requestParams = getSelectedInvestigationsIds();
        } else {
            editEmployee = getEmployeeFromModalWindow();
            editEmployee.employeeId = originalEmployee.employeeId;
            requestParams = editEmployee;
            isRedraw = true;
        }

        let successfulRequestFunction = (employee, alertAreaElementId, isRedrawEmployee) => {
            return function (result) {
                debugger;
                daHelper.drawMessage(alertAreaElementId, "Data was successful changed.",
                    daHelper.MESSAGE_TYPE.success, daHelper.DEFAULT_ERROR_MESSAGE_ALIVE_TIME_IN_SEC);

                if (isRedrawEmployee) redrawEmployee(employee);
                $("#employeeModal").modal("hide");

                let rating = getRating(employee.employeeId);
                $("#" + daHelper.EMPLOYEE_ID_PREFIX + employee.employeeId).parent()
                    .find(".list_item-rating_container").children().eq(1).text(rating);
            }
        };

        debugger;
        let employeeLoader = new daHelper.DataLoader("PUT", url,
            successfulRequestFunction(editEmployee, "alerts_area", isRedraw), failureRequestFunction("alerts_area"),
            JSON.stringify(requestParams), null, "application/json;charset=UTF-8", null);
        employeeLoader.loadData();
    });
}

function getRating(employeeId) {

    console.log("getRating(employeeId)");

    debugger;

    let defaultRating = "N/A";

    let offset = 0;
    let isFind = false;

    for (let employeeElement of document.getElementById("containerForEmployees").children) {

        let id = employeeElement.firstElementChild.firstElementChild.id;
        if (id === daHelper.EMPLOYEE_ID_PREFIX + employeeId) {
            isFind = true;
            break;
        }
        offset++;
    }

    if (!isFind) return defaultRating;

    let responseRatingsActions = ((employeeId) => {
        return {
            successfulRequestFunction: function (result) {

                debugger;
                let rating = employeeId === result[0].first ? result[0].second : "N/A";
                drawEmployeeRating(result[0].first, rating);
            }
        }
    })(employeeId);

    let ratingLoader = new daHelper.DataLoader("GET", daHelper.EMPLOYEES_URL + daHelper.RATING_URL,
        responseRatingsActions.successfulRequestFunction, failureRequestFunction("alerts_area"),
        {offset: offset, limit: 1}, {Accept: "application/json"}, null, "text json");
    ratingLoader.loadData();
}


function showParticipatedInvestigations(employeeElement) {

    console.log("showParticipatedInvestigations(employeeElement)");

    debugger;

    let employeeId = employeeElement.firstElementChild.firstElementChild.id.split(daHelper.EMPLOYEE_ID_PREFIX)[1];

    let successfulResponseFunction = function () {

        return function (result) {

            debugger;
            enableLoadingAnimationInParticipatedInvestigationsTable(false);

            let returnedObjects = daHelper.getArrayOfObjects(result);
            if (returnedObjects.length !== this.requestData.limit) this.isAvailableMoreData = false;

            this.requestData.offset += returnedObjects.length;

            if (returnedObjects.length > 0) {
                drawParticipatedInvestigationsInTable(returnedObjects);

            } else if (this.requestData.offset === 0) {
                drawEmptyDataMessageInTable("No data.");
            }

            $("#participatedInvestigationsTableModalBody").on("scroll", () => {
                setInvestigationsScrollInTable(participatedInvestigationsLoader)
            });
        }
    };

    let failureRequestFunction = function (alertAreaElementId) {

        return function (jqXHR, textStatus, errorThrown) {
            debugger;

            enableLoadingAnimationInParticipatedInvestigationsTable(false);
            drawEmptyDataMessageInTable("Data is not available.");

            daHelper.drawMessage(alertAreaElementId, "Can't load data from server. Please check connection and try again.",
                daHelper.MESSAGE_TYPE.danger, daHelper.DEFAULT_ERROR_MESSAGE_ALIVE_TIME_IN_SEC);
        }
    };

    let participatedInvestigationsLoader = new daHelper.DataLoader("GET", daHelper.EMPLOYEE_INVESTIGATIONS_URL + "/" + employeeId,
        successfulResponseFunction(), failureRequestFunction("alerts_area"),
        {offset: 0, limit: DEFAULT_INVESTIGATIONS_IN_TABLE}, {Accept: "application/json"}, null, "text json");

    $("#participatedInvestigationsTable").modal("show");
    participatedInvestigationsLoader.loadData();
}

function setInvestigationsScrollInTable(loader) {

    console.log("setInvestigationsScrollInTable(loader)");

    debugger;

    if (!loader.isAvailableMoreData) return;

    let element = document.getElementById("participatedInvestigationsTableModalBody");

    let scrollHeight = element.scrollHeight;
    let scrollTop = element.scrollTop;
    let clientHeight = element.clientHeight;

    if (scrollHeight - scrollTop - clientHeight < DEFAULT_ROW_IN_TABLE_HEIGHT * DEFAULT_INVISIBLE_INVESTIGATIONS_COUNT) {

        $("#participatedInvestigationsTableModalBody").off("scroll");
        enableLoadingAnimationInParticipatedInvestigationsTable(true);
        loader.loadData();
    }
}

function drawEmptyDataMessageInTable(message) {

    console.log("drawEmptyDataMessageInTable(message)");

    let container = document.createElement("div");
    container.id = "containerForEmptyDataMessageInTable";
    container.setAttribute("style", "text-align: center; margin: 10px auto 10px;");

    document.getElementById("participatedInvestigationsTableModalBody").appendChild(container);

    let emptyMessageElement = document.createElement("h4");
    emptyMessageElement.setAttribute("style", "color: black; font-style: italic;");
    emptyMessageElement.innerText = message;
    container.appendChild(emptyMessageElement);
}

function drawParticipatedInvestigationsInTable(investigations) {

    console.log("drawParticipatedInvestigationsInTable(investigations)");

    if (!(investigations instanceof Array)) return;

    for (let investigation of investigations) {
        let tableBodyElement = document.getElementById("participatedInvestigationsTableBody");
        tableBodyElement.appendChild(createTableRowElements(investigation));
    }
}

function createTableRowElements(investigation) {

    console.log("createTableRowElements(investigation)");

    debugger;

    if (investigation.title == null) investigation.title = "";

    let startDate = moment(investigation.startInvestigationDate).format(daHelper.DATE_TIME_FORMAT);

    let endDate = moment(investigation.endInvestigationDate);
    endDate = endDate.parsingFlags().nullInput ? "" : endDate.format(daHelper.DATE_TIME_FORMAT);


    let tableRowElement = document.createElement("tr");
    let createdRowsHTML = "<td>" + investigation.number + "</td>" +
        "<td>" + investigation.title + "</td>" +
        "<td>" + startDate + daHelper.INVESTIGATION_DATE_SEPARATOR + endDate + "</td>";

    tableRowElement.insertAdjacentHTML("beforeend", createdRowsHTML);
    return tableRowElement;
}

function resetParticipatedInvestigationsTableModalDialog() {

    console.log("resetParticipatedInvestigationsTableModalDialog()");
    debugger;
    $("#participatedInvestigationsTableBody").empty();
    $("#containerForEmptyDataMessageInTable").remove();
}

function enableLoadingAnimationInParticipatedInvestigationsTable(state) {

    console.log("enableLoadingAnimationInParticipatedInvestigationsTable(state)");

    var elementDisplayMode = "none";
    if (state) elementDisplayMode = "block";

    $("#modalTableLoadingAnimation").css("display", elementDisplayMode);
}

function failureRequestFunction(alertAreaElementId) {
    console.log("failureRequestFunction(alertAreaElementId)");

    return function (jqXHR, textStatus, errorThrown) {
        debugger;
        daHelper.drawMessage(alertAreaElementId, "Can't send data to server. Please check connection and try again.",
            daHelper.MESSAGE_TYPE.danger, daHelper.DEFAULT_ERROR_MESSAGE_ALIVE_TIME_IN_SEC);
    };
}

function redrawEmployee(employee) {

    console.log("redrawEmployee(employee)");

    debugger;

    let element = document.getElementById(daHelper.EMPLOYEE_ID_PREFIX + employee.employeeId);
    let titleElement = element.parentElement;

    let dateFormat = daHelper.DATE_TIME_FORMAT.split(" ")[0];

    let ageElement = titleElement.nextElementSibling.firstElementChild.children[1];
    let startWorkingElement = titleElement.nextElementSibling.children[1].children[1];

    let age = "N/A";
    let ageDate = moment(employee.age, daHelper.SERVER_DATE_FORMAT);

    if (ageDate.isValid()) {
        age = moment().diff(ageDate, "years");
        ageDate = ageDate.format(dateFormat);
    }
    else ageDate = "N/A";

    ageElement.setAttribute("title", ageDate);
    ageElement.innerText = age;

    let month = "N/A";
    let year = "N/A";

    let startWorkingDate = moment(employee.startWorkingDate, daHelper.SERVER_DATE_FORMAT);

    if (startWorkingDate.isValid()) {
        year = moment().diff(startWorkingDate, "years");
        month = moment().diff(startWorkingDate, "months") % 12;
        startWorkingDate = startWorkingDate.format(dateFormat);
    } else {
        startWorkingDate = "N/A";
    }

    startWorkingElement.setAttribute("title", startWorkingDate);
    startWorkingElement.innerText = year + daHelper.EMPLOYEE_YEAR_MONTH_SEPARATOR + month + daHelper.EMPLOYEE_YEAR_MONTH_POSTFIX;
}

function getEmployeeFromModalWindow() {

    console.log("getEmployeeFromModalWindow()");

    debugger;

    let name = $("#employeeName").val();

    let dateFormat = daHelper.DATE_TIME_FORMAT.split(" ")[0];

    let ageDate = moment($("#employeeAgeDate").val(), dateFormat).utc().format(daHelper.SERVER_DATE_FORMAT);
    let startWorkingDate = moment($("#employeeWorkingDate").val(), dateFormat).utc().format(daHelper.SERVER_DATE_FORMAT);

    let participatedInvestigations = [];
    let investigationsIds = getSelectedInvestigationsIds();

    for (let i = 0; i < investigationsIds.length; i++) {
        participatedInvestigations.push({
            investigationId: parseInt(investigationsIds[i], 10),
            number: daHelper.TEMPLATE_INVESTIGATION_NUMBER,
            title: daHelper.TEMPLATE_INVESTIGATION_TITLE,
            startInvestigationDate: daHelper.TEMPLATE_INVESTIGATION_START_INVESTIGATION_DATE,
            endInvestigationDate: daHelper.TEMPLATE_INVESTIGATION_END_INVESTIGATION_DATE,
            description: daHelper.TEMPLATE_INVESTIGATION_DESCRIPTION,
        });
    }

    return {
        employeeId: null,
        name: name,
        age: ageDate,
        startWorkingDate: startWorkingDate,
        participatedInvestigations: participatedInvestigations
    };
}

function getSelectedInvestigationsIds() {

    console.log("getSelectedInvestigationsIds()");
    debugger;

    let selectedInvestigationsIds = [];
    let participatedInvestigationsContainerElement = document.getElementById("ms-" + "investigations").children[1].children[1];
    let selectedElements = participatedInvestigationsContainerElement.children;

    for (let element of selectedElements) {
        if (daHelper.hasClass(element, "ms-selected")) {
            debugger;
            selectedInvestigationsIds.push(element.id.split("-selection")[0]);
        }
    }
    return selectedInvestigationsIds;
}

function isSameEmployee(employee) {

    console.log("isSameEmployee(employee)");

    let name = $("#employeeName").val();
    let age = $("#employeeAgeDate").val();
    let startWorkingDate = $("#employeeWorkingDate").val();

    if (employee.name === name && employee.age === age
        && employee.startWorkingDate === startWorkingDate) {
        return true;
    }
    return false;
}

function checkValidation() {

    console.log("checkValidation()");
    debugger;

    let arrayOfCheckedElements = [$("#employeeName")[0], $("#employeeAgeDate")[0], $("#employeeWorkingDate")[0]];

    for (let element of arrayOfCheckedElements) {
        if (!isSuccessfulValidationState(element)) {
            element.focus();
            return false;
        }
    }
    return true;
}

function initEmployeeModalWindow() {

    console.log("initEmployeeModalWindow()");

    daHelper.settingDatePickers("employeeAgeDate", "employeeWorkingDate");

    $("#employeeName")[0].CustomValidation = new CustomValidation($("#employeeName")[0], nameValidityCheck, EMPLOYEE_FIELD_TYPE.NAME);
    // $("#employeeName").on("input", $("#employeeName")[0].CustomValidation.checkValidity);
    $("#employeeName").on("change", () => {
        $("#employeeName")[0].CustomValidation.checkValidity();
    });

    $("#employeeAgeDate")[0].CustomValidation = new CustomValidation($("#employeeAgeDate")[0], ageValidityCheck, EMPLOYEE_FIELD_TYPE.AGE_DATE);
    $("#employeeAgeDate").on("change", () => {
        $("#employeeAgeDate")[0].CustomValidation.checkValidity();
    });

    $("#employeeWorkingDate")[0].CustomValidation = new CustomValidation($("#employeeWorkingDate")[0], startWorkingDateValidityCheck, EMPLOYEE_FIELD_TYPE.START_WORKING_DATE);
    $("#employeeWorkingDate").on("change", () => {
        $("#employeeWorkingDate")[0].CustomValidation.checkValidity();
    });
}

function prepareEditEmployeeModalWindow(element) {

    console.log("prepareEditEmployeeModalWindow(element)");

    debugger;

    $("#modal_title").text("Edit employee");

    let employeeId = $(element).find(".list_item-title").children(":first").attr("id").split(daHelper.EMPLOYEE_ID_PREFIX)[1];
    let name = $(element).find(".list_item_title-name").text();

    let age = $(element).find(".list_item_date-age").children().eq(1).attr("title");
    let workingDate = $(element).find(".list_item_date-start_working").children().eq(1).attr("title");

    $("#employeeName").val(name);
    $("#employeeAgeDate").val(age);
    $("#employeeWorkingDate").val(workingDate);

    $("#employeeName")[0].CustomValidation.checkValidity();
    $("#employeeAgeDate")[0].CustomValidation.checkValidity();
    $("#employeeWorkingDate")[0].CustomValidation.checkValidity();

    daHelper.settingDatePickers("employeeAgeDate", "employeeWorkingDate");

    return {
        employeeId: parseInt(employeeId, 10),
        name,
        age: age !== "N/A" ? age : null,
        startWorkingDate: workingDate !== "N/A" ? workingDate : null,
    };
}

function removeEmployee(element) {

    console.log("removeEmployee(element) - with id: " + element.firstElementChild.firstElementChild.id.split(daHelper.EMPLOYEE_ID_PREFIX)[1]);

    debugger;

    let employeeId = element.firstElementChild.firstElementChild.id.split(daHelper.EMPLOYEE_ID_PREFIX)[1];

    $("#confirmModal").modal("show");

    $("#confirmOk").off('click').on('click', function () {

        $("#confirmModal").addClass("loading");

        var removeDataLoader = new daHelper.DataLoader("DELETE", daHelper.EMPLOYEES_URL + "/" + employeeId,
            (result) => {

                element.remove();
                $("#confirmModal").modal("hide").removeClass("loading");
                daHelper.drawMessage("alerts_area", "Employee removed.",
                    daHelper.MESSAGE_TYPE.success, daHelper.DEFAULT_ERROR_MESSAGE_ALIVE_TIME_IN_SEC);
            },
            (jqXHR, textStatus, errorThrown) => {

                $("#confirmModal").modal("hide").removeClass("loading");
                daHelper.drawMessage("alerts_area", "Can't remove employee. Please reset connection and reload page.",
                    daHelper.MESSAGE_TYPE.danger, daHelper.DEFAULT_ERROR_MESSAGE_ALIVE_TIME_IN_SEC);
            }, null, null, null, null);

        removeDataLoader.loadData();
    });

    $("#confirmCancel").off('click').on('click', function () {
        $("#confirmModal").modal("hide");
    });
}

function allInvestigationsSuccessfulResponse(element) {

    return function (result) {
        debugger;
        daHelper.enableLoadingAnimationInMultiselect(element, false);

        let returnedObjects = daHelper.getArrayOfObjects(result);
        if (returnedObjects.length !== this.requestData.limit) this.isAvailableMoreData = false;

        this.requestData.offset += returnedObjects.length;

        if (returnedObjects.length > 0) {
            for (let investigation of returnedObjects) {
                drawInvestigation(investigation);
            }
        }
    }
}

function investigationsFailureResponse(element, alertAreaElementId) {
    return function (jqXHR, textStatus, errorThrown) {
        debugger;
        daHelper.enableLoadingAnimationInMultiselect(element, false);

        daHelper.drawMessage(alertAreaElementId, "Can't load data from server. Please check connection and reload page.",
            daHelper.MESSAGE_TYPE.danger, daHelper.DEFAULT_ERROR_MESSAGE_ALIVE_TIME_IN_SEC);
    }
}

function drawParticipatedInvestigations(investigations) {

    console.log("drawParticipatedInvestigations(employees)");

    if (investigations == null) investigations = [];
    if (investigations.constructor !== Array) investigations = [investigations];

    for (let investigation of investigations) {

        debugger;

        if (!isInvestigationDraw(investigation)) drawInvestigation(investigation);
        setInvestigationSelected(investigation);
    }
}

function drawInvestigation(investigation) {

    console.log("drawInvestigation(investigation)");

    debugger;

    if (isInvestigationDraw(investigation)) return;

    $("#investigations").append(
        $('<option>').attr('value', investigation.investigationId)
            .text(INVESTIGATION_NUMBER_PREFIX + investigation.number + '   ' + investigation.title)
    );

    // $("#ms-investigations").children(":first").append(
    $("#ms-investigations").children(":first").children().eq(1).append(
        $('<li>').addClass(" ms-elem-selectable").attr("id", investigation.investigationId + "-selectable")
            .html(`<span>${INVESTIGATION_NUMBER_PREFIX} ${investigation.number} ${investigation.title}</span>`)
            .css("display", "list-item")
    );


    $("#ms-investigations").children().eq(1).children().eq(1).append(
        $('<li>').addClass(" ms-elem-selection").attr("id", investigation.investigationId + "-selection")
            .html(`<span>${INVESTIGATION_NUMBER_PREFIX} ${investigation.number}   ${investigation.title}</span>`)
            .hide()
    );

    $("#investigations").multiSelect("addExistsElementsByIdOrValue", "id=" + investigation.investigationId);
}

function setInvestigationsScroll(element, loader) {

    console.log("setInvestigationsScroll(element, loader)");

    debugger;

    if (!loader.isAvailableMoreData) return;

    let scrollHeight = element.scrollHeight;
    let scrollTop = element.scrollTop;
    let clientHeight = element.clientHeight;

    if (scrollHeight - scrollTop - clientHeight < DEFAULT_INVESTIGATION_ELEMENT_HEIGHT * DEFAULT_INVISIBLE_INVESTIGATIONS_COUNT) {

        daHelper.enableLoadingAnimationInMultiselect(element.parentElement, true);
        loader.loadData();
    }
}

function setInvestigationSelected(investigation) {

    console.log("setInvestigationSelected(investigation)");

    $("#" + investigation.investigationId + "-selectable").addClass(" ms-selected").hide();
    $("#" + investigation.investigationId + "-selection").addClass(" ms-selected").css("display", "list-item");
}

function isInvestigationDraw(investigation) {

    console.log("isInvestigationDraw(investigation)");

    debugger;
    var isDraw = false;

    for (let investigationElement of document.getElementById("investigations").children) {
        if (parseInt(investigationElement.value, 10) === investigation.investigationId) {
            isDraw = true;
            break;
        }
    }
    return isDraw;
}

function getCountEmployeesInPage() {

    console.log("getCountEmployeesInPage()");

    let headerElement = $("header");
    let footerElement = $("footer");
    let titleElement = $("main").first().first();

    let investigationAreaHeight = $(document).height() - headerElement.height() - titleElement.height() - footerElement.height();
    let maxInvestigationCount = Math.ceil(investigationAreaHeight / DEFAULT_EMPLOYEE_ELEMENT_HEIGHT);

    return maxInvestigationCount + DEFAULT_INVISIBLE_EMPLOYEE_COUNT_ON_PAGE;
}

function drawEmployeeRating(employeeId, rating) {

    console.log("drawEmployeeRating(employeeId, rating)");

    debugger;
    $("#" + daHelper.EMPLOYEE_ID_PREFIX + employeeId).parent().parent()
        .find(".list_item-rating_container").children().eq(1).text(rating);
}

function initEmployeesLoading() {

    console.log("initEmployeesLoading()");

    debugger;

    let responseRatingsActions = ((alertAreaElementId) => {
        return {
            successfulRequestFunction: function (result) {

                // Returned value is [{first: 1, second: 0},{first:2, second: 66}]
                debugger;
                for (let pair of result) {
                    drawEmployeeRating(pair.first, pair.second);
                }
            },
            failureRequestFunction: function (jqXHR, textStatus, errorThrown) {

                debugger;
                daHelper.drawMessage(alertAreaElementId, "Can't send data to server. Please check connection and try again.",
                    daHelper.MESSAGE_TYPE.danger, daHelper.DEFAULT_ERROR_MESSAGE_ALIVE_TIME_IN_SEC);
            }
        };
    })("alerts_area");

    let ratingLoader = new daHelper.DataLoader("GET", daHelper.EMPLOYEES_URL + daHelper.RATING_URL,
        responseRatingsActions.successfulRequestFunction, responseRatingsActions.failureRequestFunction,
        {offset: 0, limit: getCountEmployeesInPage()}, {Accept: "application/json"}, null, "text json");

    let responseActions = new daHelper.GetDataResponseAction("containerForEmployees", "alerts_area", null);

    debugger;

    responseActions.successfulRequestFunction = ((dataAreaElementId, drawDataFunction) => {
        return function (result) {

            debugger;
            enableLoadingAnimation(false);

            let returnedObjects = daHelper.getArrayOfObjects(result);
            if (returnedObjects.length != this.requestData.limit) this.isAvailableMoreData = false;

            let offset = this.requestData.offset;
            this.requestData.offset += returnedObjects.length;

            if (this.requestData.offset === 0) {

                if (!daHelper.checkOldEmptyDataMessages(dataAreaElementId, daHelper.DEFAULT_NO_DATA_MESSAGE)) {
                    daHelper.drawEmptyData(dataAreaElementId, daHelper.DEFAULT_NO_DATA_MESSAGE);
                }
            } else {
                drawDataFunction(dataAreaElementId, returnedObjects);

                ratingLoader.requestData.offset = offset;
                ratingLoader.requestData.limit = returnedObjects.length;
                ratingLoader.loadData();
            }

            $(window).on('scroll', setEmployeesScroll(employeeLoader));
        }
    })("containerForEmployees", daHelper.drawEmployees);

    let employeeLoader = new daHelper.DataLoader("GET", daHelper.EMPLOYEES_URL,
        responseActions.successfulRequestFunction, responseActions.failureRequestFunction,
        {offset: 0, limit: getCountEmployeesInPage()}, {Accept: "application/json"}, null, "text json");

    employeeLoader.loadData();
    // $(window).on('onscroll', setEmployeesScroll(employeeLoader));
}

function setEmployeesScroll(loader) {
    console.log("employeeScroll(loader)");

    return () => {

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

        if ((scrollHeight - scrollTop - clientHeight) < DEFAULT_EMPLOYEE_ELEMENT_HEIGHT + footerHeight) {
            daHelper.enableLoadingAnimation(true);
            loader.loadData();
            $(window).off('scroll');
        }
    };
}

function enableLoadingAnimation(state) {

    console.log("enableLoadingAnimation(state)");

    var elementDisplayMode = "none";
    if (state) elementDisplayMode = "block";

    $("#loading_animation").css("display", elementDisplayMode);
}

function removePickerData(removeButtonElement) {
    debugger;
    console.log("removePickerData(fieldType, removeButtonElement)");

    let element = removeButtonElement.nextElementSibling;

    if (element.tagName === "DIV") element = element.firstElementChild;

    element.value = "";
    element.CustomValidation.resetValidation();
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

function setElementValidation(element, fieldType, isValid) {

    console.log("setElementValidation(element, isValid)");
    debugger;

    if (hasElementValidationState(element)) return;

    let arrayPreferences = isValid ? daHelper.arrayValidationSuccessPref : daHelper.arrayValidationErrorPref;

    let validationElementContainer = document.createElement("div");

    if (fieldType === EMPLOYEE_FIELD_TYPE.AGE_DATE || fieldType === EMPLOYEE_FIELD_TYPE.START_WORKING_DATE) {
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
    elementOldParent.appendChild(validationElementContainer);
}

function isSuccessfulValidationState(element) {

    console.log("isElementValidationSuccessful(element)");

    if (!hasElementValidationState(element)) {
        element.CustomValidation.checkValidity();
    }
    return daHelper.hasClass(element.parentElement, daHelper.arrayValidationSuccessPref[0]);
}

function CustomValidation(inputField, validityCheck, fieldType) {
    this.inputField = inputField;
    this.validityCheck = validityCheck;
    this.fieldType = fieldType;
}

CustomValidation.prototype = {
    constructor: CustomValidation,
    checkValidity: function () {

        debugger;
        // if (hasElementValidationState(this.inputField)) resetValidation(this.inputField, this.fieldType);
        if (hasElementValidationState(this.inputField)) this.resetValidation();

        if (this.validityCheck.isInvalid(this.inputField)) {
            //  draw Message and get onfocus event;
            setElementValidation(this.inputField, this.fieldType, false);
            this.inputField.setCustomValidity(this.validityCheck.invalidityMessage);

        } else {
            setElementValidation(this.inputField, this.fieldType, true);
        }
    },
    resetValidation: function () {

        debugger;

        if (!hasElementValidationState(this.inputField)) return;

        let oldParentElement = this.inputField.parentElement.parentElement;
        this.inputField.nextElementSibling.remove();
        this.inputField.nextElementSibling.remove();
        this.inputField.parentElement.remove();

        this.inputField.className = "form-control";

        if (this.fieldType === EMPLOYEE_FIELD_TYPE.AGE_DATE || this.fieldType === EMPLOYEE_FIELD_TYPE.START_WORKING_DATE) {
            this.inputField.className += " modal_input-datetimepicker";
        } else {
            this.inputField.className += " modal-input";
        }

        this.inputField.removeAttribute("aria-describedby");
        oldParentElement.appendChild(this.inputField);
    }
};


let nameValidityCheck = {
    isInvalid: (input) => {

        console.log("nameValidityChecks - inInvalid(input)");
        debugger;
        return !XRegExp('^\\pL+[\\pL\\pZ\\pP\.\'-]*$').test(input.value);
    },
    invalidityMessage: "Name can't start from number or have special symbol."
};

let ageValidityCheck = {
    isInvalid: (input) => {

        console.log("ageValidityChecks - inInvalid(input)");
        debugger;

        let pickedMoment = moment(input.value, daHelper.DATE_TIME_FORMAT);
        return !(pickedMoment.isValid() ? moment().diff(pickedMoment, "years") >= 21 : false);
    },
    invalidityMessage: "Age must be a least 21 years."
};

let startWorkingDateValidityCheck = {
    isInvalid: (input) => {

        console.log("startWorkingDateValidityChecks - inInvalid(input)");
        debugger;

        let pickedMoment = moment(input.value, daHelper.DATE_TIME_FORMAT);
        let ageDate = moment(document.getElementById("employeeAgeDate").value, daHelper.DATE_TIME_FORMAT);
        return !(pickedMoment.isValid() && ageDate.isValid() ? ageDate.isBefore(pickedMoment) : false);
    },
    invalidityMessage: "Start working date must be early than age date."

};