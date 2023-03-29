const { DataTypes } = require("sequelize");

const sequelize = require("../config");
const Post = require("./post.model");
const User = require("./user.model");

const Rating = sequelize.define(
    "Rating",
    {
        id: {
            type: DataTypes.INTEGER,
            autoIncrement: true,
            primaryKey: true,
            allowNull: false,
        },
        score: {
            type: DataTypes.INTEGER,
            allowNull: false,
        },
        content: {
            type: DataTypes.TEXT,
            allowNull: false,
        },
    },
    {
        tableName: "ratings",
    }
);

User.belongsToMany(Post, {
    through: Rating,
    foreignKey: "rating_user_id",
    as: "rating_posts",
});

Post.belongsToMany(User, {
    through: Rating,
    foreignKey: "post_id",
    as: "ratings",
});

(async () => {
    await Rating.sync();
})();

// Post.belongsToMany(Ratting, {
//     through: "posts_ratting",
//     as: "ratting",
//     foreignKey: "post_id",
//     otherKey: "ratting_id",
// });

module.exports = Rating;
