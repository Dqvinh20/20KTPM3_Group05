class ResponseType {
  constructor() {
    this.status = null;
    this.data = null;
    this.error = null;
  }

  static Success(data) {
    var response = new ResponseType();
    response.status = "success";
    response.data = data;
    return response;
  }

  static Failure(error) {
    var response = new ResponseType();
    response.status = "failure";
    response.error = error;
    return response;
  }

  static Error(error) {
    var response = new ResponseType();
    response.status = "error";
    response.error = error;
    return response;
  }
}

module.exports = ResponseType;
