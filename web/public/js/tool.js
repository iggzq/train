Tool = {

    isEmpty: (obj) => {
        if((typeof obj === 'string')){
            return !obj || obj.replace(/\s+/g,"") === ""
        }else {
            return (!obj || JSON.stringify(obj) === "{}" || obj.length === 0);
        }
    },


    isNotEmpty: (obj) => {
        return !Tool.isEmpty(obj);
    },


    copy: (obj) => {
        if(Tool.isNotEmpty(obj)){
            return JSON.parse(JSON.stringify(obj));
        }
    }


}