const { ValidationError } = require("sequelize");
const jwt = require("jsonwebtoken");
const AuthService = require("../services/auth.service");
const UserService = require("../services/user.service");
const ResponseType = require("../models/response.model");

const login = async (req, res) => {
  const { email, password } = req.body;

  try {
    let user = await UserService.getUserByEmail(email);
    if (!user) {
      return res.json(ResponseType.Failure("E-mail or password incorrect"));
    }
    if (!user.checkPassword(password, user.password)) {
      return res.json(ResponseType.Failure("E-mail or password incorrect"));
    }

    const token = jwt.sign(
      { id: user.id, email: user.email },
      process.env.JWT_SECRET,
      {
        expiresIn: process.env.JWT_EXPIRES_IN,
      }
    );

    user = user.toJSON();
    delete user.password;
    delete user.createdAt;
    delete user.updatedAt;
    delete user.tokens;

    return res.json(ResponseType.Success({ token, user }));
  } catch (error) {
    if (error instanceof ValidationError) {
      return res.json(
        ResponseType.Error({
          name: error.name,
          msg: error.errors[0].message,
        })
      );
    }
    return res.json(ResponseType.Error(error.message));
  }
};

const signup = async (req, res) => {
  const { email, password, name } = req.body;
  try {
    await UserService.createUser({ email, password, name });
    return res.status(201).json(ResponseType.Success("User created"));
  } catch (error) {
    if (error instanceof ValidationError) {
      return res.json(
        ResponseType.Error({
          name: error.name,
          msg: error.errors[0].message,
        })
      );
    }
    return res.json(ResponseType.Error(error.message));
  }
};

const resetPassword = async (req, res) => {
  const { email } = req.body;
  try {
    const user = await AuthService.resetPassword(email);
    res.json(ResponseType.Success(user));
  } catch (error) {
    if (error instanceof ValidationError) {
      return res.json(
        ResponseType.Error({
          name: error.name,
          msg: error.errors[0].message,
        })
      );
    }
    return res.json(ResponseType.Error(error.message));
  }
};

module.exports = {
  login,
  signup,
  resetPassword,
};
