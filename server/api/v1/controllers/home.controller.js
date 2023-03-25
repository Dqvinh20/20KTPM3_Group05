const HomeService = require("../services/home.service");

const MIN_LIMIT = 10;
const MAX_LIMIT = 20;

const addLikedByYou = async (posts, userId) => {
  try {
    await Promise.all(
      posts.map(async (post) => {
        post.setDataValue("is_liked_by_you", await post.hasLiked_by(userId));
        return post;
      })
    );

    return posts;
  } catch (error) {
    console.log(error);
  }
};

const paginate = (page, limit, count, { ...data }) => {
  const maxPage = Math.ceil(count / limit);
  if (page > maxPage) {
    return {
      page: maxPage,
      nextPage: maxPage,
      prevPage: maxPage - 1,
      maxPage: maxPage,
      limit: limit,
      data: [
        {
          error: "Out of range",
          message: "No more data",
        },
      ],
    };
  }

  return {
    page: page,
    nextPage: page < maxPage ? Number(page) + 1 : maxPage,
    prevPage: page > 1 ? Number(page) - 1 : 1,
    maxPage: maxPage,
    limit: limit,
    ...data,
  };
};

const getHome = async (req, res) => {
  const { limit } = req.query;
  let popularPosts = await HomeService.getPopularPosts({ limit });
  let newestPosts = await HomeService.getNewestPosts({ limit });
  popularPosts = await addLikedByYou(popularPosts, req.user.id);
  newestPosts = await addLikedByYou(newestPosts, req.user.id);
  return res.send({ popular_posts: popularPosts, newest_posts: newestPosts });
};

const getPopularPosts = async (req, res) => {
  try {
    let { limit } = req.query;
    let result = await HomeService.getPopularPosts({ limit });
    result = await addLikedByYou(result, req.user.id);
    return res.send(result);
  } catch (error) {
    res.send({ error });
  }
};

const getNewestPosts = async (req, res) => {
  try {
    let { limit, page } = req.query;
    if (!page) page = 1;
    else page = Number(page);
    const offset = (page - 1) * limit;

    if (!limit) limit = MIN_LIMIT;
    else {
      limit = Number(limit);
      if (limit > MAX_LIMIT) limit = MAX_LIMIT;
    }

    let result = await HomeService.getNewestPosts({ limit, offset });
    result = await addLikedByYou(result, req.user.id);
    const count = await HomeService.countAllNewestPosts();

    result = paginate(page, limit, count, {
      data: result,
    });

    return res.send(result);
  } catch (error) {
    res.send({ error });
  }
};

module.exports = {
  getHome,
  getPopularPosts,
  getNewestPosts,
};
