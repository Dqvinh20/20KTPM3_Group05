require("dotenv").config();

const isProduction = process.env.NODE_ENV === "production";
const connectionString = `postgresql://${process.env.DB_USER}:${process.env.DB_PASSWORD}@${process.env.DB_HOST}:${process.env.DB_PORT}/${process.env.DB_DATABASE}`;
const prodConnectionString = `postgresql://${process.env.DB_USER_PROD}:${process.env.DB_PASSWORD_PROD}@${process.env.DB_HOST_PROD}:${process.env.DB_PORT_PROD}/${process.env.DB_DATABASE_PROD}`;

const { Sequelize } = require("sequelize");
const sequelize = new Sequelize(
    isProduction ? prodConnectionString : connectionString,
    {
        ssl: isProduction,
        logging: false,
    }
);

// sequelize   //dưng xoa nhe an cat do
//     .sync() // Create tables if they don't exist
//     .then(() => console.log("Database & tables created!"));

sequelize
    .authenticate()
    .then(() => console.log("Connection has been established successfully."))
    .catch((err) => console.error("Unable to connect to the database:", err));

module.exports = sequelize;
