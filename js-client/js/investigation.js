var PREFIX_URL = "http://localhost:8088/api/v1";
var INVESTIGATION_URL = "/investigations";
var INVESTIGATION_FILTER_URL = "/investigations/filter";

var EMPLOYEE_URL = "/employees";
var INVESTIGATION_EMPLOYEES_URL = "/employees/investigation";


var INVESTIGATION_NUMBER_PREFIX = "№ ";
var TIME_FORMAT = "DD.MM.YYYY HH:mm";
var DEFAULT_INVESTIGATION_ELEMENT_HEIGHT = 178;
var DEFAULT_INVISIBLE_INVESTIGATION_COUNT_ON_PAGE = 1;
var DEFAULT_ERROR_MESSAGE_ALIVE_TIME_IN_SEC = 5;

var MESSAGE_TYPE = {
    success: "alert-success",
    warning: "alert-warning",
    danger: "alert-danger"
};

var isAvailableMoreInvestigations = true;
var isAvailableMoreEmployees = true;


Element.prototype.remove = function () {
    this.parentElement.removeChild(this);
}

NodeList.prototype.remove = HTMLCollection.prototype.remove = function () {

    for (var i = this.length - 1; i >= 0; i--) {
        if (this[i] && this[i].parentElement) this[i].parentElement.removeChild(this[i]);
    }
}

$(function () {

    console.log("onload() function");

    settingDateTimePickers();
    getInvestigations();
});

function getFilteredInvestigations(startDate, endDate) {

    console.log("getFilteredInvestigations(startDate, endDate)");

    var offsetFilterInvestigations = 0;
    var investigationCount = getCountInvestigationsInPage();

    // JSON.stringify()

    var getFilteredDataFromServer = function () {

        var requestParams = {
            startInvestigationDate: startDate,
            endInvestigationDate: endDate,
            offset: offsetFilterInvestigations,
            limit: investigationCount
        };

        sendRequest2Server("GET", INVESTIGATION_FILTER_URL, function (result) {

                enableLoadingAnimation(false);

                var returnedInvestigations = getArrayOfObjects(result);
                if (returnedInvestigations.length != investigationCount) isAvailableMoreInvestigations = false;

                offsetFilterInvestigations += returnedInvestigations.length;

                if (offsetFilterInvestigations === 0) {
                    drawEmptyData("No filter data.", document.getElementById("containerForInvestigations"))
                } else {
                    drawInvestigations(returnedInvestigations);
                }
            },
            function (jqXHR, textStatus, errorThrown) {

                enableLoadingAnimation(false);

                drawMessage("Can't load data from server. Please check connection and reload page.",
                    MESSAGE_TYPE.danger, DEFAULT_ERROR_MESSAGE_ALIVE_TIME_IN_SEC);
                drawEmptyData("No data available.", document.getElementById("containerForInvestigations"));
            },
            requestParams, {Accept: "application/json"}, null, "text json");
    };

    getFilteredDataFromServer();
    setScrollUpdateInvestigationsFunction(getFilteredDataFromServer);
}

function getInvestigations() {

    console.log("getInvestigations()");

    var offsetInvestigations = 0;
    var investigationCount = getCountInvestigationsInPage();

    // JSON.stringify()

    var getDataFromServer = function () {

        var requestParams = {
            offset: offsetInvestigations,
            limit: investigationCount
        };

        sendRequest2Server("GET", INVESTIGATION_URL, function (result) {

                enableLoadingAnimation(false);

                debugger;

                var returnedInvestigations = getArrayOfObjects(result);
                if (returnedInvestigations.length != investigationCount) isAvailableMoreInvestigations = false;

                offsetInvestigations += returnedInvestigations.length;

                if (offsetInvestigations === 0) {
                    drawEmptyData("No Data.", document.getElementById("containerForInvestigation"));
                } else {
                    drawInvestigations(returnedInvestigations);
                }
            },
            function (jqXHR, textStatus, errorThrown) {

                enableLoadingAnimation(false);
                debugger;
                drawEmptyData("No data available.", document.getElementById("containerForInvestigations"));
                drawMessage("Can't load data from server. Please check connection and reload page.",
                    MESSAGE_TYPE.danger, DEFAULT_ERROR_MESSAGE_ALIVE_TIME_IN_SEC);
            },
            requestParams, {Accept: "application/json"}, null, "text json");
    };

    getDataFromServer();
    setScrollUpdateInvestigationsFunction(getDataFromServer);
}

function setScrollUpdateInvestigationsFunction(updateDataFunction) {

    console.log("setScrollUpdateInvestigationsFunction()");

    // $(".scroll-area").onscroll = function () {
    // or for full screeen
    // $(window).scroll(function);

    window.onscroll = function () {
        console.log("onscroll event");

        debugger;

        if (!isAvailableMoreInvestigations) return;

        var scrollHeight = Math.max(
            document.body.scrollHeight, document.documentElement.scrollHeight,
            document.body.offsetHeight, document.documentElement.offsetHeight,
            document.body.clientHeight, document.documentElement.clientHeight
        );

        var scrollTop = window.pageYOffset || document.documentElement.scrollTop;
        var clientHeight = document.documentElement.clientHeight;

        var footerHeight = getElementHeight(document.getElementsByTagName("footer").item(0));

        if ((scrollHeight - scrollTop - clientHeight) < DEFAULT_INVESTIGATION_ELEMENT_HEIGHT + footerHeight) {

            console.log("loading data");

            enableLoadingAnimation(true);
            updateDataFunction();
        }

    };
}

function sendRequest2Server(typeOfRequest, url, successfulFunction, failureFunction,
                            requestData, headers, contentType, dataType) {

    console.log("sendRequest2Server(typeOfRequest, url, successfulFunction, failureFunction, " +
        "requestData, headers, contentType, dataType)");

    debugger;

    typeOfRequest = isString(typeOfRequest) ? typeOfRequest : "GET";
    url = isString(url) ? url : "";
    headers = headers instanceof Object ? headers : {};
    contentType = isString(contentType) ? contentType : false;
    dataType = isString(dataType) ? dataType : "*";

    $.ajax({
            type: typeOfRequest,
            url: PREFIX_URL + url,
            headers: headers,
            dataType: dataType,
            contentType: contentType,
            data: requestData,
            success: successfulFunction,
            error: failureFunction
        }
    );
}

function enableLoadingAnimation(state) {

    console.log("enableLoadingAnimation(state)");

    // document.getElementById("loading_animation").style.display = "none";
    // document.getElementById("loading_animation").style.display = "block";

    var elementDisplayMode = "none";
    if (state) elementDisplayMode = "block";

    $("#loading_animation").css("display", elementDisplayMode);
}

function getElementHeight(element) {

    console.log("getElementHeight(Element)");

    var elementRect = element.getBoundingClientRect();
    return elementRect.bottom - elementRect.top;
}

function getArrayOfObjects(objects) {

    console.log("getArrayOfObjects(objects)");

    if (objects == null) objects = [];
    if (!(objects instanceof Array)) objects = [objects];

    return objects;
}

function isString(s) {
    return typeof(s) === "string" || s instanceof String;
}

function getCountInvestigationsInPage() {

    console.log("getCountInvestigationsInPage()");

    var clientHeight = document.documentElement.clientHeight;

    var headerElement = document.getElementsByTagName("header").item(0);
    var footerElement = document.getElementsByTagName("footer").item(0);
    var titleElement = document.getElementsByTagName("main").item(0).firstElementChild.firstElementChild;

    var footerElementHeight = getElementHeight(footerElement);
    var investigationAreaHeight = clientHeight - getElementHeight(headerElement) - getElementHeight(titleElement) - footerElementHeight;
    var maxInvestigationCount = Math.ceil(investigationAreaHeight / DEFAULT_INVESTIGATION_ELEMENT_HEIGHT);

    return maxInvestigationCount + DEFAULT_INVISIBLE_INVESTIGATION_COUNT_ON_PAGE;
}

function drawInvestigations(investigations) {

    console.log("drawInvestigations(investigations)");

    if (!(investigations instanceof Array)) return;

    investigations.forEach(function (item) {
        createInvestigationHTMLElementInDOM(item)
    });
}

function createInvestigationHTMLElementInDOM(investigation) {

    console.log("createInvestigationHTMLElement(investigation)");

    debugger;
    if (investigation.name == null) investigation.name = "";

    var endInvestigationDate = moment(investigation.endInvestigationDate);
    endInvestigationDate = endInvestigationDate.parsingFlags().nullInput ? "" : endInvestigationDate.format(TIME_FORMAT);

    var investigationItemHTML =
        "<section class=\"list_item investigation\">" +
        "<section class=\"list_item-title\">" +
        "<strong hidden id=\"investigation_id\">" + investigation.investigationId + "</strong>" +
        "<strong class=\"list_item_title-number\">" + INVESTIGATION_NUMBER_PREFIX + investigation.number + "</strong>" +
        "<strong class=\"list_item_title-name\">" + investigation.title + "</strong>" +
        "<section class=\"list_item-title-action\">" +
        "<i class=\"material-icons list_item_title-button\"" +
        "onclick=\"removeInvestigation(this.parentElement.parentElement.parentElement)\"" +
        "style=\"cursor: pointer\">delete</i>" +
        "<i class=\"material-icons list_item_title-button\"" +
        "onclick=\"editInvestigation(this.parentElement.parentElement.parentElement)\"" +
        "style=\"cursor: pointer\">mode_edit</i>" +
        "</section>" +
        "</section>" +
        "<section class=\"list_item-date group\">" +
        "<strong>start date:</strong>" +
        "<span>  " + moment(investigation.startInvestigationDate).format(TIME_FORMAT) + " / " + endInvestigationDate + "</span>" +
        "</section>" +
        "<section>" +
        "<p>" + investigation.description + "</p>" +
        "</section>" +
        "<article><hr><h5>List of involved employees</h5></article>" +
        "</section>";

    // $("#loading_animation").before(investigationItemHTML);
    var loadingAnimationElement = document.getElementById("loading_animation");
    loadingAnimationElement.insertAdjacentHTML("beforeBegin", investigationItemHTML);
}

function drawMessage(message, type, alertTimeInSec) {

    console.log("drawMessage(message, type, alertTimeInSec)");

    type = type === MESSAGE_TYPE.warning ? type : ( type === MESSAGE_TYPE.danger ? type : MESSAGE_TYPE.success);

    var alertElement = createAlertHTMLElementInDOM(message, type);
    var $alertElement = $(alertElement);

    var alerts = $("#alerts_area").children();
    for (var i = 0; i < alerts.length - 1; i++) {
        alerts[i].animate({top: "+" + Math.ceil($alertElement.outerHeight())});
    }

    $alertElement.toggle("slow", function () {
        setTimeout(function () {
            $alertElement.toggle("slow");
        }, alertTimeInSec * 1000);
    });
}

function createAlertHTMLElementInDOM(message, typeClass) {

    console.log("createAlertHTMLElementInDOM()");

    var elementAlertsArea = document.getElementById("alerts_area");

    var innerAlertElements = "<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-label=\"Close\">" +
        "<span aria-hidden=\"true\">&times;</span>" +
        "</button>" + message;

    var alertElement = document.createElement("div");
    alertElement.className = "alert " + typeClass + " alert-dismissible";
    alertElement.setAttribute("role", "alert");
    alertElement.innerHTML = innerAlertElements;

    return elementAlertsArea.appendChild(alertElement);
}

function drawEmptyData(message, element) {

    console.log("drawEmptyData(message)");

    debugger;

    var container = document.createElement("div");
    container.setAttribute("style", "text-align: center; margin: 25px auto 10px; padding: 25px 10px 10px ;");
    container.setAttribute("id", "containerForEmptyData");
    element.appendChild(container);

    var emptyMessageElement = document.createElement("h3");
    emptyMessageElement.innerText = message;
    container.appendChild(emptyMessageElement);
}

function drawEmployees(employees) {

    console.log("drawEmployee(employees)");

    if (!(employees instanceof Array)) return;

    employees.forEach(function (item) {
        createEmployeeHTMLElementInDOM(item);
    });

}

function createEmployeeHTMLElementInDOM(employee) {

    console.log("createEmployeeHTMLElementInDOM(employee)");

    debugger;

    var involvedStaffElement = document.getElementById("involvedStaff");
    var optionElement = document.createElement("option");
    optionElement.value = employee.employeeId;
    optionElement.innerHTML = employee.name;
    optionElement.checked = true;

    involvedStaffElement.appendChild(optionElement);
}

function clearInvestigationsHTMLElementsInDOM() {

    console.log("clearInvestigationHTMLElementsInDOM()");

    document.getElementById("empty");
    document.getElementsByClassName("investigation").remove();

    document.getElementById("containerForEmptyData").remove();
}


function settingDateTimePickers() {

    console.log("settingDateTimePickers()");

    var START_DATE_PICKER_ID = "start_datetimepicker";
    var END_DATE_PICKER_ID = "end_datetimepicker";

    $.datetimepicker.setDateFormatter({
        parseDate: function (date, format) {

            console.log("parseDate Function");
            console.log("Date is:" + date);
            console.log("Format is:" + format);

            var formatedDate = moment(date, format);
            return formatedDate.isValid() ? formatedDate.toDate() : false;
        },

        formatDate: function (date, format) {

            console.log("formatDate Function");
            console.log("Date is:" + date);
            console.log("Format is:" + format);

            return moment(date).format(format);
        }
    });


    var changeVisibilityOfTimeInPicker = function (pickedTime, $input) {

        console.log("change picked datetime event");

        // debugger;

        var pickedMoment = moment(pickedTime);
        if (!pickedMoment.isValid()) return;

        var currentMoment = moment();
        var maxTimeValue = moment().format("HH:mm");

        if (currentMoment.year() !== pickedMoment.year() ||
            currentMoment.month() !== pickedMoment.month() ||
            currentMoment.day() !== pickedMoment.day()) {
            maxTimeValue = false;
        }

        var minTimeValue = false;

        var otherDateTimePickerElement = $input.attr("id") !== START_DATE_PICKER_ID ? $("#" + START_DATE_PICKER_ID) : $("#" + END_DATE_PICKER_ID);
        var otherDateTimePickedMoment = moment(otherDateTimePickerElement.val(), TIME_FORMAT);

        if ($input.attr("id") === END_DATE_PICKER_ID && otherDateTimePickedMoment.isValid()
            && pickedMoment.year() === otherDateTimePickedMoment.year()
            && pickedMoment.month() === otherDateTimePickedMoment.month()
            && pickedMoment.day() === otherDateTimePickedMoment.day()) {
            minTimeValue = otherDateTimePickedMoment.format("HH:mm");
        }

        if ($input.attr("id") === START_DATE_PICKER_ID && pickedMoment.isAfter(otherDateTimePickedMoment)) {
            otherDateTimePickerElement.val(null);
        }

        this.setOptions({minTime: minTimeValue, maxTime: maxTimeValue});
    };

    var closePickerEventFunction = function (pickedTime, $input) {

        console.log("close datetimepicker event");
        console.log("close event triggered by: " + $input.attr("id"));
        // debugger;

        var otherPickerId = $input.attr("id") !== START_DATE_PICKER_ID ? START_DATE_PICKER_ID : END_DATE_PICKER_ID;
        var otherPickedMoment = moment($('#' + otherPickerId).val(), TIME_FORMAT);

        var pickedMoment = moment(pickedTime);
        if (pickedMoment.isValid()) $input.val(pickedMoment.format(TIME_FORMAT));

        $input.attr("readonly", true);

        if (pickedTime != null && $("#removeFilterButton").css("display") === "none") {
            $("#removeFilterButton").css("display", "inline-block");
        }

        if (pickedMoment.isValid() && otherPickedMoment.isValid()) {

            if ((otherPickerId === START_DATE_PICKER_ID && otherPickedMoment.isAfter(pickedMoment)) ||
                otherPickerId === END_DATE_PICKER_ID && otherPickedMoment.isBefore(pickedMoment)) {
                $input.val(null);
                return;
            }

            clearInvestigationsHTMLElementsInDOM();
            if (otherPickerId === START_DATE_PICKER_ID) getFilteredInvestigations(moment(otherPickedMoment).utc().format(), moment(pickedMoment).utc().format());
            else getFilteredInvestigations(moment(pickedMoment).utc().format(), moment(otherPickedMoment).utc().format());
        }
    };

    $("#start_datetimepicker").datetimepicker({

        format: "DD.MM.YYYY HH:mm",
        formatDate: "DD.MM.YYYY",
        formatTime: "HH:mm",
        step: 30,
        validateOnBlur: false,
        maxDate: 0,
        maxTime: moment().format("HH:mm"),

        onShow: function (pickedTime, $input) {
            $input.attr("readonly", false)
        },

        onChangeDateTime: changeVisibilityOfTimeInPicker,
        onClose: closePickerEventFunction
    });

    $("#end_datetimepicker").datetimepicker({

        format: "DD.MM.YYYY HH:mm",
        formatDate: "DD.MM.YYYY",
        formatTime: "HH:mm",
        step: 30,
        validateOnBlur: false,
        maxDate: 0,
        maxTime: moment().format("HH:mm"),
        onShow: function (pickedTime, $input) {
            this.setOptions({
                minDate: $("#start_datetimepicker").val() ? jQuery("#start_datetimepicker").val() : false
            });
            $input.attr("readonly", false);
        },

        onChangeDateTime: changeVisibilityOfTimeInPicker,
        onClose: closePickerEventFunction
    });
}

function removeFilter() {

    console.log("removeFilter()");

    // $("#start_datetimepicker").val(null);
    // $("#end_datetimepicker").val(null);
    // $("removeFilterButton").css("display", "none");

    document.getElementById("start_datetimepicker").value = null;
    document.getElementById("end_datetimepicker").value = null;

    document.getElementById("removeFilterButton").style.display = "none";

    clearInvestigationsHTMLElementsInDOM();
    getInvestigations();
}

function addInvestigation() {

    console.log("addInvestigation()");

    getEmployees();

    $("#investigationModal").modal("show");
    $("#modal_save").onclick = function () {

        validateInvestigation();
    };

}

function editInvestigation(element) {

    console.log("edit investigation with id: " + element.firstElementChild.firstElementChild.textContent);

    var modalTitleElement = document.getElementById("modal_title");
    modalTitleElement.innerText = "Edit investigation";

    debugger;

    var investigationElements = element.children;

    var investigationNumberElement = investigationElements[0].children[1];
    var investigationTitleElement = investigationNumberElement.nextElementSibling;
    var investigationDescriptionElement = investigationElements[2].firstElementChild;

    document.getElementById("investigationNumber").value = (investigationNumberElement.innerText.split(INVESTIGATION_NUMBER_PREFIX))[1];
    document.getElementById("investigationTitle").value = investigationTitleElement.innerText;

    document.getElementById("investigationDescription").innerText = investigationDescriptionElement.innerText;

    getEmployees();

    var requestParams = {
        offset: 0,
        limit: 14
    }

    sendRequest2Server("GET", INVESTIGATION_EMPLOYEES_URL + "/" + investigationElements[0].children[0].textContent, function (result) {

            var returnedEmployees = getArrayOfObjects(result);
            debugger;
            checkingInvolvedStaff(returnedEmployees);
            // if (offsetEmployees !== 0) {
            //     checkingInvolvedStaff(returnedEmployees);
            // }
        },
        function (jqXHR, textStatus, errorThrown) {

            drawMessage("Can't load data from server. Please check connection and reload page.", MESSAGE_TYPE.danger, 5);
        },
        requestParams, {Accept: "application/json"}, null, "text json");


    $("#investigationModal").modal("show");

    $("#modal_save").click(function () {

        validateInvestigation();

    });
}

function checkingInvolvedStaff(employees) {
    console.log("checkedInvolvedStaff()");
}

function validateInvestigation() {

    console.log("validateInvestigation");

}

function setScrollUpdateEmployeesFunction(updateDataFunction) {

    console.log("setScrollUpdateEmployeesFunction()");

    debugger;

    var ITEM_EMPLOYEE_ELEMENT_HEIGHT = 17;

    // document.getElementById("involvedStaff").onscroll = function () { };

    var investigationStaffElement = document.getElementById("involvedStaff");

    $("#involvedStaff").scroll(function () {

        console.log("onscroll event - involvedStaff");

        if (!isAvailableMoreEmployees) return;

        if (investigationStaffElement.scrollHeight - investigationStaffElement.clientHeight -
            investigationStaffElement.scrollTop < ITEM_EMPLOYEE_ELEMENT_HEIGHT * 4) {

            console.log("select element - onscroll event");
            updateDataFunction();
        }

    });
}

function getEmployees() {

    console.log("getEmployees()");

    var offsetEmployees = 0;
    var employeeCount = 14;

    // JSON.stringify()

    var getDataFromServer = function () {

        var requestParams = {
            offset: offsetEmployees,
            limit: employeeCount
        };

        sendRequest2Server("GET", EMPLOYEE_URL, function (result) {

                var returnedEmployees = getArrayOfObjects(result);
                if (returnedEmployees.length != employeeCount) isAvailableMoreEmployees = false;

                offsetEmployees += returnedEmployees.length;

                if (offsetEmployees === 0) {
                    drawEmptyData("List of employees is empty.", document.getElementById("involvedStaff"));
                } else {
                    drawEmployees(returnedEmployees);
                }
            },
            function (jqXHR, textStatus, errorThrown) {

                drawMessage("Can't load data from server. Please check connection and reload page.", MESSAGE_TYPE.danger, 5);
            },
            requestParams, {Accept: "application/json"}, null, "text json");
    };

    getDataFromServer();
    setScrollUpdateEmployeesFunction(getDataFromServer);
}

function removeInvestigation(element) {

    console.log("remove investigation with id: " + element.firstElementChild.firstElementChild.innerText);

    debugger;

    var investigationId = element.firstElementChild.firstElementChild.innerText;

    $("#confirmModal").modal("show");

    var test = $("#confirmOk").attr("onclick");


    $("#confirmOk").off("click");
    $("#confirmOk").click(function () {

        $("#confirmModal").addClass("loading");
        sendRequest2Server("DELETE", INVESTIGATION_URL + "/" + investigationId, function (result) {

                debugger;
                element.remove();
                $("#confirmModal").modal("hide").removeClass("loading");
                drawMessage("Investigation removed.", MESSAGE_TYPE.success, DEFAULT_ERROR_MESSAGE_ALIVE_TIME_IN_SEC);
            },
            function (jqXHR, textStatus, errorThrown) {

                debugger;
                $("#confirmModal").modal("hide").removeClass("loading");
                drawMessage("Can't remove investigation. Please check connection and reload page.",
                    MESSAGE_TYPE.danger, DEFAULT_ERROR_MESSAGE_ALIVE_TIME_IN_SEC);
            }, null, null, null, null);
    });

    $("#confirmCancel").off("click");
    $("#confirmCancel").click(function () {
        $("#confirmModal").modal("hide");
    });
}