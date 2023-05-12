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
    hooks: {
      afterFind: async (ratings) => {
        return await Promise.all(
          ratings.map(async (rating) => {
            const user_id = rating.dataValues.rating_user_id;
            delete rating.dataValues.rating_user_id;
            const user = await User.findByPk(user_id, {
              attributes: [
                "id",
                "email",
                "avatar",
                "user_name",
                "name_non_accent",
                "name",
              ],
            });
            rating.setDataValue("author", user);
            return rating;
          })
        );
      },
    },
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

module.exports = Rating;
