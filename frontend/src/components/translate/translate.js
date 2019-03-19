class Translate {
    static translate(translationKey, params = {}) {
        switch(translationKey) {
            case "missing": return "Value is required";
            case "min_max": return `Length needs to be between [${params[0]}, ${params[1]}]`;
            case "min": return `Value needs to be at least ${params[0]}`;
            case "dont_match": return "Passwords don't match";
            case "complex_error": return "Password must contain at least one special, one lower case, one upper case and one number character and no more than 2 consecutive identical characters";
            case "too_similar": return "Password is too similar to the old one";
            case "login_failed": return "Invalid username or password";
            case "start_after_end": return "Start time cannot be after end time";
            case "from_after_to": return "From date cannot be before To"
            case "unknown_role": return "Unknown role";
            case "name_already_exists": return "Name already exists";
            default: return "Unknown error";
        }
    }

    static translateErrors(errorFields) {
        for(let errorField in errorFields) {
            errorFields[errorField] = this.translate(errorFields[errorField].msg, errorFields[errorField].params);
        }
        return errorFields;
    }
  }
  
  export default Translate;
  