var daHelper = (function () {

    var publicInterface = {};

    var PREFIX_URL = "http://localhost:8088/api/v1";
    publicInterface.INVESTIGATIONS_URL = "/investigations";
    publicInterface.INVESTIGATIONS_FILTER_URL = "/investigations/filter";

    publicInterface.EMPLOYEES_URL = "/employees";
    publicInterface.INVESTIGATION_EMPLOYEES_URL = "/employees/investigation";
    publicInterface.EMPLOYEE_INVESTIGATIONS_URL = "/investigations/employee";
    publicInterface.RATING_URL = "/rating";
    publicInterface.STAFF_URL = "/staff";

    publicInterface.LOAD_ERROR_MESSAGE = "Can't load data from server. Please check connection and reload page.";
    publicInterface.SEND_ERROR_MESSAGE = "Can't send data to  server. Please check connection and try again.";

    publicInterface.SERVER_DATE_FORMAT = "YYYY-MM-DD";
    publicInterface.DATE_TIME_FORMAT = "DD.MM.YYYY HH:mm";

    publicInterface.INVESTIGATION_ID_PREFIX = "investigation_id_";
    publicInterface.INVESTIGATION_NUMBER_PREFIX = "№ ";
    publicInterface.INVESTIGATION_DATE_SEPARATOR = " / ";

    publicInterface.EMPLOYEE_ID_PREFIX = "employee_id_";
    publicInterface.EMPLOYEE_YEAR_MONTH_SEPARATOR = " / ";
    publicInterface.EMPLOYEE_YEAR_MONTH_POSTFIX = " (Y/M)";


    publicInterface.DEFAULT_ERROR_MESSAGE_ALIVE_TIME_IN_SEC = 5;
    publicInterface.DEFAULT_NO_DATA_MESSAGE = "No data.";
    publicInterface.DEFAULT_NO_AVAILABLE_DATA_MESSAGE = "No data available.";

    publicInterface.TEMPLATE_INVESTIGATION_NUMBER = 777;
    publicInterface.TEMPLATE_INVESTIGATION_TITLE = "TEMPLATE_TITLE";
    publicInterface.TEMPLATE_INVESTIGATION_START_INVESTIGATION_DATE = "2017-07-19T00:00:00Z";
    publicInterface.TEMPLATE_INVESTIGATION_END_INVESTIGATION_DATE = "2017-07-20T00:00:00Z";
    publicInterface.TEMPLATE_INVESTIGATION_DESCRIPTION = "TEMPLATE_DESCRIPTION";

    publicInterface.TEMPLATE_EMPLOYEE_NAME = "Jock Doe";
    publicInterface.TEMPLATE_EMPLOYEE_AGE = "1992-05-26";
    publicInterface.TEMPLATE_EMPLOYEE_START_WORKING_DATE = "2017-07-20";

    publicInterface.MESSAGE_TYPE = {
        success: "alert-success",
        warning: "alert-warning",
        danger: "alert-danger"
    };

    publicInterface.arrayValidationSuccessPref = ["has-success", "Success2Status", "glyphicon-ok", "(success)"];
    publicInterface.arrayValidationErrorPref = ["has-error", "Error2Status", "glyphicon-remove", "(error)"];

    (function () {
        // For IE 8 compatibility
        function inherit(proto) {
            function F() {
            };
            F.prototype = proto;
            var object = new F;
            return object;
        };

        if (!Object.create) Object.create = inherit;

        Element.prototype.remove = function () {
            this.parentElement.removeChild(this);
        }
        NodeList.prototype.remove = HTMLCollection.prototype.remove = function () {

            for (var i = this.length - 1; i >= 0; i--) {
                if (this[i] && this[i].parentElement) this[i].parentElement.removeChild(this[i]);
            }
        }
    })();

    publicInterface.drawInvestigations = function (elementId, investigations) {

        console.log("drawInvestigations(elementId, investigations)");

        if (!(investigations instanceof Array)) return;

        var investigationsElementContainer = document.getElementById(elementId);

        investigations.forEach(function (item) {
            var elementHTML = createInvestigationHTMLElement(item);
            investigationsElementContainer.insertAdjacentHTML("beforeEnd", elementHTML);
            // $().append(createInvestigationHTMLElementInDOM(item));
        });
    };
    function createInvestigationHTMLElement(investigation) {

        console.log("createInvestigationHTMLElement(investigation)");

        debugger;
        if (investigation.title == null) investigation.title = "";

        var endInvestigationDate = moment(investigation.endInvestigationDate);
        endInvestigationDate = endInvestigationDate.parsingFlags().nullInput ? "" : endInvestigationDate.format(publicInterface.DATE_TIME_FORMAT);

        var investigationItemHTML =
            "<section class=\"list_item investigation\">" +
            "<section class=\"list_item-title\">" +
            "<strong hidden id=\"" + publicInterface.INVESTIGATION_ID_PREFIX + investigation.investigationId + "\"></strong>" +
            "<strong class=\"list_item_title-number\">" + publicInterface.INVESTIGATION_NUMBER_PREFIX + investigation.number + "</strong>" +
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
            "<span>  " + moment(investigation.startInvestigationDate).format(publicInterface.DATE_TIME_FORMAT) + publicInterface.INVESTIGATION_DATE_SEPARATOR + endInvestigationDate + "</span>" +
            "</section>" +
            "<section>" +
            "<p>" + investigation.description + "</p>" +
            "</section>" +
            "<article><hr><h5 onclick=\"showInvolvedStaff(this.parentElement.parentElement)\">List of involved employees</h5></article>" +
            "</section>";
        return investigationItemHTML;
    }


    publicInterface.drawEmployees = function (elementId, employees) {

        console.log("drawEmployee(elementId, employees)");

        if (!(employees instanceof Array)) return;

        var employeesElementContainer = document.getElementById(elementId);

        employees.forEach(function (item) {
            var element = createEmployeeHTMLElement(item);
            employeesElementContainer.insertAdjacentHTML("beforeEnd", element);
        });

    };

    function createEmployeeHTMLElement(employee) {

        console.log("createEmployeeHTMLElement(employee)");

        debugger;

        var dateFormat = daHelper.DATE_TIME_FORMAT.split(" ")[0];

        var undefinedRatingSymbol = "N/A";

        var ageDate = null;
        var age = moment(employee.age);

        if (age.isValid()) {
            age = moment().diff(age, "years");
            ageDate = moment(employee.age).format(dateFormat);
        }
        else {
            age = ageDate = "N/A";
        }

        var month = "N/A";
        var year = "N/A";

        var startWorkingDate = moment(employee.startWorkingDate);

        if (startWorkingDate.isValid()) {
            year = moment().diff(startWorkingDate, "years");
            month = moment().diff(startWorkingDate, "months") % 12;
            startWorkingDate = startWorkingDate.format(dateFormat);
        }

        var employeeItemHTML =
            "<section class=\"list_item\">" +
            "<section class=\"list_item-title\">" +
            "<strong hidden id=\"" + publicInterface.EMPLOYEE_ID_PREFIX + employee.employeeId + "\"></strong>" +
            "<strong class=\"list_item_title-name\" style=\"font-size: 28px\">" + employee.name + "</strong>" +
            "<section class=\"list_item-title-action\">" +
            "<i class=\"material-icons list_item_title-button\"" +
            "onclick=\"removeEmployee(this.parentElement.parentElement.parentElement)\"" +
            "style=\"cursor: pointer\">delete</i>" +
            "<i class=\"material-icons list_item_title-button\"" +
            "onclick=\"editEmployee(this.parentElement.parentElement.parentElement)\"" +
            "style=\"cursor: pointer\">mode_edit</i>" +
            "</section>" +
            "</section>" +
            "<section class=\"group\">" +
            "<section class=\"list_item_date-age\">" +
            "<strong>Age:</strong><span title=\"" + ageDate + "\">" + age + "</span>" +
            "</section>" +
            "<section class=\"list_item_date-start_working\">" +
            "<strong>Work experience:</strong><span title=\"" + startWorkingDate + "\">" + year + publicInterface.EMPLOYEE_YEAR_MONTH_SEPARATOR + month + publicInterface.EMPLOYEE_YEAR_MONTH_POSTFIX + "</span>" +
            "</section>" +
            "</section>" +
            "<section class=\"group\">" +
            "<section class=\"list_item-rating_container\">" +
            "<strong>Rating:</strong><span>" + undefinedRatingSymbol + "</span>" +
            "</section>" +
            "</section>" +
            "<article><hr><h5 onclick=\"showParticipatedInvestigations(this.parentElement.parentElement)\">Participated investigations</h5></article>" +
            "</section>";

        return employeeItemHTML;
    }

    publicInterface.drawEmptyData = function (elementId, message) {

        console.log("drawEmptyData(elementId, message)");

        var container = document.createElement("div");
        container.setAttribute("style", "text-align: center; margin: 25px auto 10px; padding: 25px 10px 10px ;");
        // container.setAttribute("class", "containerForEmptyData");
        container.id = "containerForEmptyDataMessage";

        document.getElementById(elementId).appendChild(container);

        var emptyMessageElement = document.createElement("h3");
        emptyMessageElement.innerText = message;
        container.appendChild(emptyMessageElement);
    };

    publicInterface.drawMessage = function (elementId, message, type, alertTimeInSec) {

        console.log("drawMessage(elementId, message, type, alertTimeInSec)");

        type = type === publicInterface.MESSAGE_TYPE.warning ? type :
            ( type === publicInterface.MESSAGE_TYPE.danger ? type : publicInterface.MESSAGE_TYPE.success);

        var alertElement = createAlertElement(message, type);
        var $alertElement = $(alertElement).appendTo("#" + elementId);

        var alerts = $("#" + elementId).children();
        for (var i = 0; i < alerts.length - 1; i++) {
            alerts[i].animate({top: "+" + Math.ceil($alertElement.outerHeight)});
        }

        $alertElement.toggle("slow", function () {
            setTimeout(function () {
                $alertElement.toggle("slow");
            }, alertTimeInSec * 1000);
        });
    };

    function createAlertElement(message, typeClass) {

        console.log("createAlertElement(message, typeClass)");

        var innerAlertElements = "<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-label=\"Close\">" +
            "<span aria-hidden=\"true\">&times;</span>" +
            "</button>" + message;

        var alertElement = document.createElement("div");
        alertElement.className = "alert " + typeClass + " alert-dismissible";
        alertElement.setAttribute("role", "alert");
        alertElement.innerHTML = innerAlertElements;

        return alertElement;
        // return elementAlertsArea.appendChild(alertElement);
    }

    publicInterface.getArrayOfEmptyDataElements = function (elementId, cls, isDeepSearch) {

        console.log("getArrayOfEmptyDataElements(elementId)");

        var elements = [];

        var findInnerElementsWithClass = function (parentElement) {
            debugger;

            var childrenElements = parentElement.children;

            for (var i = 0; i < childrenElements.length; i++) {

                if (publicInterface.hasClass(childrenElements[i], cls)) {
                    elements.push(childrenElements[i]);
                }
                else {
                    if (!isDeepSearch) continue;
                    findInnerElementsWithClass(childrenElements[i]);
                }
            }
        };

        findInnerElementsWithClass(document.getElementById(elementId));
        return elements;
    };

    publicInterface.hasClass = function (element, cls) {

        console.log("hasClass(element, cls)");
        return (" " + element.className + " ").indexOf(" " + cls + " ") > -1;
    }

    publicInterface.isNumber = function (num) {

        console.log("isNumber(num)");
        return !isNaN(num);
    };
    publicInterface.isString = function (s) {

        console.log("isString(s)");
        return typeof(s) === "string" || s instanceof String;
    };
    publicInterface.getElementHeight = function (element) {

        console.log("getElementHeight(element)");

        var elementRect = element.getBoundingClientRect();
        return elementRect.bottom - elementRect.top;
    };
    publicInterface.getArrayOfObjects = function (objects) {

        console.log("getArrayOfObjects(objects)");

        if (objects == null) objects = [];
        if (!(objects instanceof Array)) objects = [objects];

        return objects;
    };

    publicInterface.enableLoadingAnimation = function (state) {

        console.log("enableLoadingAnimation(state)");

        var elementDisplayMode = "none";
        if (state) elementDisplayMode = "block";

        $("#loading_animation").css("display", elementDisplayMode);
    };

    function hasLoadingAnimationInMultiselect(element) {

        console.log("hasLoadingAnimationInMultiselect(element)");

        debugger;
        if (element.lastElementChild !== null && element.lastElementChild.tagName === "DIV"
            && daHelper.hasClass(element.lastElementChild, "modal_multiselect_lists-loading")) {
            return true;
        }
        return false;
    }

    publicInterface.enableLoadingAnimationInMultiselect = function (element, state) {

        console.log("enableLoadingAnimationInMultiselect(element, state)");
        debugger;

        if (state) {
            if (hasLoadingAnimationInMultiselect(element)) return;

            var loadingAnimationContainer = document.createElement("div");
            loadingAnimationContainer.className = "modal_multiselect_lists-loading";
            loadingAnimationContainer.innerHTML =
                "<img class=\"modal_multiselect_lists_loading-image\" src=\"images\\refresh.gif\" alt=\"Loading...\">";

            element.children[1].style.display = "none";
            element.appendChild(loadingAnimationContainer);

        } else {
            if (!hasLoadingAnimationInMultiselect(element)) return;
            element.lastElementChild.remove();
            element.children[1].style.display = "block";
        }
    };

    publicInterface.scrollingHeaderEvent = function (headerContainerId) {
        let isScrolling = false;
        let headerContainerElement = document.getElementById(headerContainerId);
        return () => {
            debugger;
            if (!isScrolling && $(window).scrollTop() > 0) {
                headerContainerElement.className += " scrolling";
                isScrolling = true;
            } else if (isScrolling && $(window).scrollTop() === 0) {
                headerContainerElement.className = "header-container";
                isScrolling = false;
            }
        };
    }
    ;

    publicInterface.checkOldEmptyDataMessages = function (elementId, emptyDataMessage) {

        console.log("checkOldEmptyDataMessages(elementId, emptyDataMessage)");

        debugger;

        var emptyDataElements = daHelper.getArrayOfEmptyDataElements(elementId, "containerForEmptyData", false);
        var hasDrawEmptyDate = false;

        for (var i = 0; i < emptyDataElements.length; i++) {
            if (emptyDataElements[i].children[0].innerText === emptyDataMessage) hasDrawEmptyDate = true;
        }

        if (!hasDrawEmptyDate) {
            emptyDataElements.forEach(function (item) {
                item.remove();
            });
        }
        return hasDrawEmptyDate;
    };

    publicInterface.settingDateTimePickers = function (startDatetimePickerElementId, endDatetimePickerElementId,
                                                       removePickersElementId, actionFunctionWhenDatesPicked) {

        console.log("settingDateTimePickers(startDatetimePickerElementId, endDatetimePickerElementId," +
            "removePickersElementId, actionFunctionWhenDatesPicked)");

        debugger;
        var $startDatePickerElement = $("#" + startDatetimePickerElementId);
        var $endDatePickerElement = $("#" + endDatetimePickerElementId);

        $.datetimepicker.setDateFormatter({
            parseDate: function (date, format) {

                console.log("parseDate Function(date, format)");
                var formatedDate = moment(date, format);
                return formatedDate.isValid() ? formatedDate.toDate() : false;
            },

            formatDate: function (date, format) {

                console.log("formatDate Function(date, format)");
                return moment(date).format(format);
            }
        });

        var changeVisibilityOfTimeInPicker = function (pickedTime, $input) {

            debugger;
            console.log("change picked datetime event");

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

            var otherDateTimePickerElement = $input.attr("id") !== startDatetimePickerElementId ?
                $startDatePickerElement : $endDatePickerElement;
            var otherDateTimePickedMoment = moment(otherDateTimePickerElement.val(), publicInterface.DATE_TIME_FORMAT);

            if ($input.attr("id") === endDatetimePickerElementId && otherDateTimePickedMoment.isValid()
                && pickedMoment.year() === otherDateTimePickedMoment.year()
                && pickedMoment.month() === otherDateTimePickedMoment.month()
                && pickedMoment.day() === otherDateTimePickedMoment.day()) {
                minTimeValue = otherDateTimePickedMoment.format("HH:mm");
            }

            if ($input.attr("id") === startDatetimePickerElementId && pickedMoment.isAfter(otherDateTimePickedMoment)) {
                otherDateTimePickerElement.val(null);
            }

            this.setOptions({minTime: minTimeValue, maxTime: maxTimeValue});
        };

        var closePickerEventFunction = function (pickedTime, $input) {

            console.log("close datetimepicker event");

            var otherPickerId = $input.attr("id") !== startDatetimePickerElementId ?
                startDatetimePickerElementId : endDatetimePickerElementId;
            var otherPickedMoment = moment($('#' + otherPickerId).val(), publicInterface.DATE_TIME_FORMAT);

            var pickedMoment = moment(pickedTime);
            if (pickedMoment.isValid()) $input.val(pickedMoment.format(publicInterface.DATE_TIME_FORMAT));

            $input.attr("readonly", true);

            var $removePickedElement = $("#" + removePickersElementId);
            if (pickedTime != null && $removePickedElement.length > 0 && $removePickedElement.css("display") === "none") {
                $removePickedElement.css("display", "inline-block");
            }

            if (pickedMoment.isValid() && otherPickedMoment.isValid()) {

                if ((otherPickerId === startDatetimePickerElementId && otherPickedMoment.isAfter(pickedMoment)) ||
                    otherPickerId === endDatetimePickerElementId && otherPickedMoment.isBefore(pickedMoment)) {
                    $input.val(null);
                    return;
                }

                if (otherPickerId === startDatetimePickerElementId) actionFunctionWhenDatesPicked(moment(otherPickedMoment).utc().format(), moment(pickedMoment).utc().format());
                else actionFunctionWhenDatesPicked(moment(pickedMoment).utc().format(), moment(otherPickedMoment).utc().format());
            }
        };

        function equalsPickedDateInMoment(firstPickedMoment, secondPickedMoment) {

            if (firstPickedMoment && secondPickedMoment &&
                firstPickedMoment.isValid() && secondPickedMoment.isValid()
                && firstPickedMoment.year() === secondPickedMoment.year()
                && firstPickedMoment.month() === secondPickedMoment.month()
                && firstPickedMoment.day() === secondPickedMoment.day()
            ) {
                return true;
            }

            return false;
        }

        $startDatePickerElement.datetimepicker(new DefaultDateTimePickerSettings(function (pickedTime, $input) {
            $input.attr("readonly", false)
        }, changeVisibilityOfTimeInPicker, closePickerEventFunction));

        $endDatePickerElement.datetimepicker(new DefaultDateTimePickerSettings(function (pickedTime, $input) {

            debugger;

            var startPickedMoment = moment($startDatePickerElement.val(), publicInterface.DATE_TIME_FORMAT);
            var pickedMoment = moment(pickedTime);

            var minTimeValue = false;

            if (startPickedMoment.isValid()) {

                var isDateEquals = pickedMoment.isValid() ? equalsPickedDateInMoment(pickedMoment, startPickedMoment)
                    : equalsPickedDateInMoment(moment(), startPickedMoment);

                if (isDateEquals) {
                    minTimeValue = startPickedMoment.format("HH:mm");
                }
            }

            this.setOptions({
                minDate: $startDatePickerElement.val() ? $startDatePickerElement.val() : false,
                minTime: minTimeValue
            });
            $input.attr("readonly", false);
        }, changeVisibilityOfTimeInPicker, closePickerEventFunction));
    };

    publicInterface.settingDatePickers = function (ageDatePickerElementId, startWorkingDatePickerElementId) {

        console.log("settingDatePickers(ageDatePicker, startWorkingDatePicker)");

        var dateFormat = publicInterface.DATE_TIME_FORMAT.split(" ")[0];

        var $ageDatePickerElement = $("#" + ageDatePickerElementId);
        var $startWorkingDatePickerElement = $("#" + startWorkingDatePickerElementId);

        var closePickerEventFunction = function (pickedTime, $input) {

            console.log("close datetimepicker event");

            var pickedMoment = moment(pickedTime);
            if (pickedMoment.isValid()) $input.val(pickedMoment.format(dateFormat));

            $input.attr("readonly", true);

            if (typeof $ageDatePickerElement[0].CustomValidation !== typeof undefined
                && $ageDatePickerElement[0].CustomValidation != null) {
                $ageDatePickerElement[0].CustomValidation.checkValidity();
            }

            if (typeof $startWorkingDatePickerElement[0].CustomValidation !== typeof undefined
                && $startWorkingDatePickerElement[0].CustomValidation != null) {
                $startWorkingDatePickerElement[0].CustomValidation.checkValidity();
            }
        };

        debugger;

        $ageDatePickerElement.datetimepicker(new DefaultDatePickerSettings(function (pickedTime, $input) {
            debugger;
            $input.attr("readonly", false);
            this.setOptions({
                maxDate: $startWorkingDatePickerElement.val() ? $startWorkingDatePickerElement.val() : 0,
                formatDate: 'd.m.Y'
            });
        }, closePickerEventFunction));

        $startWorkingDatePickerElement.datetimepicker(new DefaultDatePickerSettings(function (pickedTime, $input) {
            debugger;
            $input.attr("readonly", false);
            this.setOptions({
                minDate: $ageDatePickerElement.val() ? $ageDatePickerElement.val() : false,
                formatDate: 'd.m.Y'
            });
        }, closePickerEventFunction));
    };


    function DefaultDatePickerSettings(onShowFunction, onCloseFunction) {

        return {
            timepicker: false,
            format: 'd.m.Y',
            validateOnBlur: false,
            maxDate: 0,
            yearStart: 1900,
            onShow: onShowFunction,
            onClose: onCloseFunction
        };
    }

    function DefaultDateTimePickerSettings(onShowFunction, onChangeDateTimeFunction, onCloseFunction) {

        return {
            format: publicInterface.DATE_TIME_FORMAT,
            formatDate: publicInterface.DATE_TIME_FORMAT.split(" ")[0],
            formatTime: publicInterface.DATE_TIME_FORMAT.split(" ")[1],
            step: 30,
            validateOnBlur: false,
            maxDate: 0,
            yearStart: 1900,
            maxTime: moment().format(publicInterface.DATE_TIME_FORMAT.split(" ")[1]),
            onShow: onShowFunction,
            onChangeDateTime: onChangeDateTimeFunction,
            onClose: onCloseFunction
        };
    }

    publicInterface.successfulResponse2GetAllElementsInMultiselect = function (element, drawElementFunction) {

        return function (result) {
            debugger;
            daHelper.enableLoadingAnimationInMultiselect(element, false);

            let returnedObjects = daHelper.getArrayOfObjects(result);
            if (returnedObjects.length !== this.requestData.limit) this.isAvailableMoreData = false;

            this.requestData.offset += returnedObjects.length;

            if (returnedObjects.length > 0) {
                for (let investigation of returnedObjects) {
                    drawElementFunction(investigation);
                }
            }
        }
    };

    publicInterface.failureResponseInMultiselect = function (element, alertAreaElementId) {
        return function (jqXHR, textStatus, errorThrown) {
            debugger;
            daHelper.enableLoadingAnimationInMultiselect(element, false);

            daHelper.drawMessage(alertAreaElementId, "Can't load data from server. Please check connection and reload page.",
                daHelper.MESSAGE_TYPE.danger, daHelper.DEFAULT_ERROR_MESSAGE_ALIVE_TIME_IN_SEC);
        }
    };


    publicInterface.failureResponseWithDrawEmptyData = function (dataAreaElementId, alertAreaElementId, message) {
        return function (jqXHR, textStatus, errorThrown) {

            debugger;
            enableLoadingAnimation(false);

            if (!publicInterface.checkOldEmptyDataMessages(dataAreaElementId, daHelper.DEFAULT_NO_AVAILABLE_DATA_MESSAGE)) {
                daHelper.drawEmptyData(dataAreaElementId, daHelper.DEFAULT_NO_AVAILABLE_DATA_MESSAGE);
            }
            daHelper.drawMessage(alertAreaElementId, message,
                daHelper.MESSAGE_TYPE.danger, daHelper.DEFAULT_ERROR_MESSAGE_ALIVE_TIME_IN_SEC);
        }
    };

    publicInterface.failureResponse = function (alertAreaElementId, message) {
        return function (jqXHR, textStatus, errorThrown) {

            debugger;
            daHelper.drawMessage(alertAreaElementId, message,
                daHelper.MESSAGE_TYPE.danger, daHelper.DEFAULT_ERROR_MESSAGE_ALIVE_TIME_IN_SEC);
        }
    };


    publicInterface.DataLoader = function (typeOfRequest, url, successfulFunction, failureFunction,
                                           requestData, headers, contentType, dataType) {

        var typeOfRequest = publicInterface.isString(typeOfRequest) ? typeOfRequest : "GET";
        var url = publicInterface.isString(url) ? url : "";
        var headers = headers instanceof Object ? headers : {};
        var contentType = publicInterface.isString(contentType) ? contentType : false;
        var dataType = publicInterface.isString(dataType) ? dataType : "*";

        this.requestData = requestData;
        this.isAvailableMoreData = true;

        this.loadData = function () {
            debugger;
            $.ajax({
                    type: typeOfRequest,
                    url: PREFIX_URL + url,
                    headers: headers,
                    dataType: dataType,
                    contentType: contentType,
                    data: this.requestData,
                    success: successfulFunction.bind(this),
                    error: failureFunction.bind(this)
                }
            );
        }
    };


    publicInterface.CustomValidation = function (inputField, validityCheck, fieldType) {
        this.inputField = inputField;
        this.validityCheck = validityCheck;
        this.fieldType = fieldType;
    };

    publicInterface.CustomValidation.prototype = {
        constructor: publicInterface.CustomValidation,
        checkValidity: function () {

            debugger;
            if (hasElementValidationState(this.inputField)) this.resetValidation();

            if (this.validityCheck.isInvalid(this.inputField)) {
                //  draw Message and get onfocus event;
                setElementValidation(this.inputField, this.fieldType, false, this.validityCheck.invalidityMessage);
                this.inputField.setCustomValidity(this.validityCheck.invalidityMessage);

            } else {
                setElementValidation(this.inputField, this.fieldType, true);
            }
        },
        resetValidation: function () {
            resetElementValidation(this.inputField, this.fieldType)
        }
    };

    return publicInterface;

})();
