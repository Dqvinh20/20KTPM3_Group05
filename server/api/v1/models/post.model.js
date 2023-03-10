const { DataTypes } = require("sequelize");
const sequelize = require("../config");

const User = require("./user.model");

const Post = sequelize.define(
    "Post",
    {
        id: {
            type: DataTypes.INTEGER,
            autoIncrement: true,
            primaryKey: true,
            allowNull: false,
        },
        title: {
            type: DataTypes.STRING,
        },
        brief_description: {
            type: DataTypes.STRING,
        },
        cover_img: {
            type: DataTypes.STRING,
            defaultValue: "https://picsum.photos/200/300",
        },
        is_public: {
            type: DataTypes.BOOLEAN,
            defaultValue: false,
        },
        view_count: {
            type: DataTypes.INTEGER,
            defaultValue: 0,
        },
        like_count: {
            type: DataTypes.INTEGER,
            defaultValue: 0,
        },
        avg_rating: {
            type: DataTypes.DOUBLE,
            defaultValue: 0,
        },
        rating_count: {
            type: DataTypes.INTEGER,
            defaultValue: 0,
        },
        start_date: {
            type: DataTypes.DATE,
            defaultValue: new Date(),
        },
        end_date: {
            type: DataTypes.DATE,
            defaultValue: new Date(),
        },
    },
    { tableName: "posts" }
);

User.hasMany(Post, {
    foreignKey: {
        name: "created_by",
        allowNull: false,
    },
});

Post.belongsTo(User, {
    as: "author",
    foreignKey: {
        name: "created_by",
        allowNull: false,
    },
});

Post.belongsToMany(User, {
    through: "users_posts_like",
    as: "liked_by",
    foreignKey: "post_id",
    otherKey: "user_id",
});

User.belongsToMany(Post, {
    through: "users_posts_like",
    as: "liked_posts",
    foreignKey: "user_id",
    otherKey: "post_id",
});

(async () => {
    // await Post.sync({ force: true });
    // await Post.create({
    //   title: "Post 1",
    //   brief_description: "Post 1 brief description",
    //   created_by: 1,
    // });
    // await sequelize.models.users_posts_like.create({
    //   user_id: 1,
    //   post_id: 1,
    // });
    // await Post.findByPk(1, {
    //   attributes: {
    //     exclude: ["created_by"],
    //   },
    //   include: [
    //     {
    //       model: User,
    //       as: "liked_by",
    //       attributes: ["id", "email"],
    //       through: { attributes: [] },
    //     },
    //     {
    //       model: User,
    //       as: "author",
    //       attributes: ["id", "email"],
    //     },
    //   ],
    // }).then((post) => console.log(JSON.stringify(post, null, 2)));
    // await User.findByPk(1, {
    //   // attributes: {
    //   //   exclude: ["created_by"],
    //   // },
    //   include: [
    //     {
    //       model: Post,
    //       as: "liked_posts",
    //       attributes: {
    //         exclude: ["created_by"],
    //       },
    //       through: { attributes: [] },
    //     },
    //   ],
    // }).then((post) => console.log(JSON.stringify(post, null, 2)));
})();

module.exports = Post;
