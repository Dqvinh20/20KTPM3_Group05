const { Op } = require("sequelize");

const Post = require("../models/post.model");
const User = require("../models/user.model");
const UserFollow = User.sequelize.models.user_followers;

const queryAuthor = {
  association: "author",
  attributes: { exclude: ["password"] },
};

const commonPost = (opts) => ({
  attributes: { exclude: ["created_by", "title_non_accent"] },
  where: { is_public: true },
  include: [{ ...queryAuthor }],
  ...opts,
});

const getPopularPosts = async ({ offset, limit }) => {
  const result = await Post.findAll(
    commonPost({
      order: [
        ["view_count", "DESC"],
        ["like_count", "DESC"],
      ],
      offset: offset ? offset : 0,
      limit,
    })
  );

  return result;
};

// const countAllNewestPosts = async () => {
//     const count = await Post.count({
//         where: { is_public: true },
//         order: [["createdAt", "DESC"]],
//     });
//     return count;
// };

// const getNewestPosts = async ({ offset, limit }) => {
//     const result = await Post.findAll(
//         commonPost({
//             order: [["createdAt", "DESC"]],
//             offset: offset ? offset : 0,
//             limit,
//         })
//     );
//     return result;
// };

const getNewestPostsOfFollowings = async (
  followings,
  { where, limit, offset }
) => {
  // Get 3 day newest public posts of followings
  const post = await Post.findAndCountAll(
    commonPost({
      include: [
        {
          ...queryAuthor,
          where: {
            id: {
              [Op.in]: followings,
            },
          },
          required: true,
        },
      ],
      where,
      order: [["createdAt", "DESC"]],
    })
  );
  return post;
};

const getNewestPosts = async (followings, { where, limit, offset }) => {
  // Get 3 day newest public posts
  const post = await Post.findAndCountAll(
    commonPost({
      include: [
        {
          ...queryAuthor,
          where: {
            id: {
              [Op.notIn]: followings,
            },
          },
          required: true,
        },
      ],
      where,
      order: [["createdAt", "DESC"]],
    })
  );
  return post;
};

const getNewsFeed = async (user_id, { offset, limit, ...opts }) => {
  const today = new Date();
  const _3daysAgo = new Date(today);
  _3daysAgo.setDate(today.getDate() - 3);
  try {
    let followings = await UserFollow.findAll({
      attributes: [["following_id", "id"]],
      where: {
        follower_id: user_id,
      },
    });

    followings = followings.map((f) => Number(f.id));
    const followingPosts = await getNewestPostsOfFollowings(followings, {
      where: {
        is_public: true,
        [Op.not]: {
          created_by: user_id,
        },
        createdAt: {
          [Op.between]: [_3daysAgo, today],
        },
      },
    });

    const newestPosts = await getNewestPosts(followings, {
      where: {
        is_public: true,
        [Op.not]: {
          created_by: user_id,
        },
        // createdAt: {
        //   [Op.between]: [_3daysAgo, today],
        // },
      },
    });

    // [followingPosts, newestPosts]
    let posts = [...followingPosts.rows, ...newestPosts.rows];
    const count = followingPosts.count + newestPosts.count;
    console.log(offset, limit, count);
    posts = posts.slice(offset, offset + limit);

    return { count, row: posts };
  } catch (error) {
    console.log(error);
  }
};

module.exports = {
  getPopularPosts,
  getNewsFeed,
  // getNewestPosts,
  // countAllNewestPosts,
};
