SESSION_ORDER = "SESSION_ORDER";
SESSION_TICKET_PARAMS = "SESSION_TICKET_PARAMS";
SESSION_CONFIRM_SEAT_TYPES = "SESSION_CHOICE_SEAT_TYPES";
SESSION_CONFIRM_TICKETS = "SESSION_CONFIRM_TICKETS";
SESSION_CONFIRM_COLUMNS = "SESSION_CONFIRM_COLUMNS";
SESSION_TOTAL_MONEY = "SESSION_TOTAL_MONEY";
SESSION_DEADLINE_TIME = "SESSION_DEADLINE_TIME";
SESSION_PAY_INFO = "TICKET_PAY_INFO";


SessionStorage = {
    get: function (key) {
        const v = sessionStorage.getItem(key);
        if (v && typeof (v) !== "undefined" && v !== "undefined") {
            return JSON.parse(v);
        }
    },
    set: function (key, data) {
        sessionStorage.setItem(key, JSON.stringify(data));
    },
    remove: function (key) {
        sessionStorage.removeItem(key);
    },
    clearAll: function () {
        sessionStorage.clear();
    }
};
