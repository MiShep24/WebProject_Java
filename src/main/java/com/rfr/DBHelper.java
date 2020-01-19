package com.rfr;

import java.io.Closeable;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DBHelper implements Closeable {
    private Connection connection = null;

    /**
     * Создает экземпляр объекта и подключение к базе данных. Также создает таблицу, если ее не существует
     */
    DBHelper() {
        try {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://127.0.0.1:5432/postgres?loggerLevel=OFF";
            connection = DriverManager.getConnection(url, Constants.login, Constants.password);
            createUsersTable();
            createUsersInfoTable();
            createPostsTable();
            createMembersTable();
            createLikesTable();
            createCommentsTable();
            createBlogTable();
            createLikesBlogTable();
            createCommentsBlogTable();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Метод создает таблицу пользователей, если она не существует
     *
     * @throws SQLException
     */
    private void createUsersTable() throws SQLException {
        PreparedStatement statmt = connection.prepareStatement("CREATE TABLE if not exists users (id serial primary key , firstName text, lastName text, middleName text, birthday text, passworduser text, emailuser text, photo text);");
        statmt.execute();
    }

    /**
     * Метод создает таблицу доп информации о пользователях, если она не существует
     *
     * @throws SQLException
     */
    private void createUsersInfoTable() throws SQLException {
        PreparedStatement statmt = connection.prepareStatement("CREATE TABLE if not exists users_info (user_id serial primary key , city text, school text, rang text, trener text, about text);");
        statmt.execute();
    }

    /**
     * Метод создает таблицу постов, если она не существует
     *
     * @throws SQLException
     */
    private void createPostsTable() throws SQLException {
        PreparedStatement statmt = connection.prepareStatement("CREATE TABLE if not exists posts (pid serial primary key , uid integer, name text, age text, p_date text, location text, level text, info text);");
        statmt.execute();
    }

    /**
     * Метод создает таблицу участников соревнования, если она не существует
     *
     * @throws SQLException
     */
    private void createMembersTable() throws SQLException {
        PreparedStatement statmt = connection.prepareStatement("CREATE TABLE if not exists members (id serial primary key , uid integer, pid integer);");
        statmt.execute();
    }

    /**
     * Метод создает таблицу лайков, если она не существует
     *
     * @throws SQLException
     */
    private void createLikesTable() throws SQLException {
        PreparedStatement statmt = connection.prepareStatement("CREATE TABLE if not exists likes (id serial primary key , uid integer, pid integer);");
        statmt.execute();
    }

    /**
     * Метод создает таблицу комментов, если она не существует
     *
     * @throws SQLException
     */
    private void createCommentsTable() throws SQLException {
        PreparedStatement statmt = connection.prepareStatement("CREATE TABLE if not exists comments (id serial primary key , uid integer, pid integer, time_send text, text_com text);");
        statmt.execute();
    }


    /**
     * Метод создает таблицу постов блога, если она не существует
     *
     * @throws SQLException
     */
    private void createBlogTable() throws SQLException {
        PreparedStatement statmt = connection.prepareStatement("CREATE TABLE if not exists blog_posts (id serial primary key , uid integer, photo_post text, text_com text, title text);");
        statmt.execute();
    }

    /**
     * Метод создает таблицу комментов блога, если она не существует
     *
     * @throws SQLException
     */
    private void createCommentsBlogTable() throws SQLException {
        PreparedStatement statmt = connection.prepareStatement("CREATE TABLE if not exists comments_blog (id serial primary key , uid integer, pid integer, time_send text, text_com text);");
        statmt.execute();
    }

    /**
     * Метод создает таблицу лайков блога, если она не существует
     *
     * @throws SQLException
     */
    private void createLikesBlogTable() throws SQLException {
        PreparedStatement statmt = connection.prepareStatement("CREATE TABLE if not exists likes_blog (id serial primary key , uid integer, pid integer);");
        statmt.execute();
    }

    /**
     * Метод добавляет пользователя в таблицу
     *
     * @param user - пользователь
     * @return возвращает объект для дальнейшего закрытия подключения
     * @throws SQLException
     */
    DBHelper addUser(User user) throws SQLException {
        if (getUser(user.getEmailuser()) != null) {
            throw new SQLException("This email already exist!");
        }
        PreparedStatement st = connection.prepareStatement("INSERT INTO users (firstName, lastName, middleName, birthday, passworduser, emailuser, photo) values(?, ?, ?, ?, ?, ?, ?);");
        setParams(user, st);

        st.execute();

        return this;

    }

    /**
     * Метод обновляет данные о пользователе в бд (если сменилась аватарка и тд)
     *
     * @param user
     * @return
     */
    DBHelper updateUser(User user) {
        try {
            PreparedStatement st = connection.prepareStatement("UPDATE users set firstName = ?, lastName= ?, middleName= ?, birthday= ?, passworduser= ?, emailuser= ?, photo= ? WHERE id = ?;");
            int k = setParams(user, st);
            st.setInt(k, user.getId());
            st.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;

    }

    public DBHelper setUserInfo(int uid, UserInfo userInfo) {
        if (getUserInfo(uid) != null) {
            updateUserInfo(uid, userInfo);
        } else {
            try {
                PreparedStatement st = connection.prepareStatement("INSERT into users_info (user_id, city, school, rang, trener, about) values (?, ?, ?, ?, ?, ?);");
                int k = 1;
                st.setInt(k++, uid);
                st.setString(k++, userInfo.getCity());
                st.setString(k++, userInfo.getSchool());
                st.setString(k++, userInfo.getRang());
                st.setString(k++, userInfo.getTrener());
                st.setString(k++, userInfo.getAbout());
                st.execute();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public UserInfo getUserInfo(int uid) {
        try {
            PreparedStatement st = connection.prepareStatement("SELECT * FROM users_info WHERE user_id = ?;");
            st.setInt(1, uid);
            ResultSet resultSet = st.executeQuery();
            boolean next = resultSet.next();
            if (!next) return null;
            return new UserInfo(resultSet.getString("city"),
                    resultSet.getString("school"),
                    resultSet.getString("rang"),
                    resultSet.getString("trener"),
                    resultSet.getString("about"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateUserInfo(int uid, UserInfo userInfo) {
        try {
            PreparedStatement st = connection.prepareStatement("UPDATE users_info set city = ?, school= ?, rang= ?, trener= ?, about= ? WHERE user_id = ?;");
            int k = 1;
            st.setString(k++, userInfo.getCity());
            st.setString(k++, userInfo.getSchool());
            st.setString(k++, userInfo.getRang());
            st.setString(k++, userInfo.getTrener());
            st.setString(k++, userInfo.getAbout());
            st.setInt(k++, uid);
            st.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private int setParams(User user, PreparedStatement st) throws SQLException {
        int k = 1;
        st.setString(k++, user.getFirstName());
        st.setString(k++, user.getLastName());
        st.setString(k++, user.getMiddleName());
        st.setString(k++, user.getBirthday());
        st.setString(k++, user.getPassworduser());
        st.setString(k++, user.getEmailuser());
        st.setString(k++, user.getAvatar());
        return k;
    }


    /**
     * Метод позволяет получить пользователя из базы данных
     *
     * @param email - email пользователя
     * @return - Пользователь
     */
    User getUser(String email) {
        try {
            PreparedStatement st = connection.prepareStatement("SELECT * FROM users WHERE emailuser = ?;");
            st.setString(1, email);
            return getUser(st);
        } catch (Exception e) {
            e.printStackTrace();
            close();
            return null;
        }
    }

    /**
     * Метод позволяет получить пользователя из базы данных
     *
     * @param id - id пользователя
     * @return - Пользователь
     */
    User getUser(int id) {
        try {
            PreparedStatement st = connection.prepareStatement("SELECT * FROM users WHERE id = ?;");
            st.setInt(1, id);
            return getUser(st);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private User getUser(PreparedStatement st) throws SQLException {
        ResultSet resultSet = st.executeQuery();
        if (!resultSet.next()) return null;
        int k = 1;
        User user = new User(resultSet.getInt(k++), resultSet.getString(k++), resultSet.getString(k++), resultSet.getString(k++), resultSet.getString(k++), resultSet.getString(k++), resultSet.getString(k++), resultSet.getString(k++));
        UserInfo userInfo = getUserInfo(user.getId());
        user.setUserInfo(userInfo);
        return user;
    }

    public DBHelper addPost(Post post) {
        try {
            PreparedStatement st = connection.prepareStatement("INSERT INTO posts (uid, name, age, p_date, location, level, info) values(?, ?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            int k = 1;
            st.setInt(k++, post.getUid());
            st.setString(k++, post.getName());
            st.setString(k++, post.getAge());
            st.setString(k++, post.getDate());
            st.setString(k++, post.getLocation());
            st.setString(k++, post.getLevel());
            st.setString(k, post.getInfo());
            st.executeUpdate();
            ResultSet generatedKeys = st.getGeneratedKeys();
            generatedKeys.next();

            addMember(generatedKeys.getInt(1), post.getUid());

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public ArrayList<Post> getAllPosts(int uid) {
        try {
            PreparedStatement st = connection.prepareStatement("SELECT * FROM posts");
            ResultSet resultSet = st.executeQuery();
            ArrayList<Post> posts = new ArrayList<>();

            while (resultSet.next()) {
                int pid = resultSet.getInt("pid");
                ArrayList<Integer> members = getMembers(pid);
                Post post = new Post(pid, resultSet.getInt("uid"),
                        resultSet.getString("name"), resultSet.getString("age"),
                        resultSet.getString("p_date"), resultSet.getString("location"),
                        resultSet.getString("level"), resultSet.getString("info"), members, getLikesCount(pid));
                post.setMember(members.contains(uid));
                posts.add(post);
            }
            close();
            return posts;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        close();
        return new ArrayList<>();
    }

    public ArrayList<Post> getPosts(int uid) {
        try {
            PreparedStatement st = connection.prepareStatement("SELECT * FROM posts where uid=?");
            st.setInt(1, uid);
            ResultSet resultSet = st.executeQuery();
            ArrayList<Post> posts = new ArrayList<>();

            while (resultSet.next()) {
                int pid = resultSet.getInt("pid");
                ArrayList<Integer> members = getMembers(pid);
                Post post = new Post(pid, resultSet.getInt("uid"),
                        resultSet.getString("name"), resultSet.getString("age"),
                        resultSet.getString("p_date"), resultSet.getString("location"),
                        resultSet.getString("level"), resultSet.getString("info"), members, getLikesCount(pid));
                post.setMember(members.contains(uid));
                posts.add(post);
            }
            close();
            return posts;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        close();
        return new ArrayList<>();
    }


    public ArrayList<Post> getPostIfMember(int uid) {
        ArrayList<Post> posts = new ArrayList<>();

        try {
            PreparedStatement st = connection.prepareStatement("SELECT * FROM members where uid=?");
            st.setInt(1, uid);
            ResultSet resultSet = st.executeQuery();
            ArrayList<Integer> integers = new ArrayList<>();
            while (resultSet.next()) {
                integers.add(resultSet.getInt("pid"));
            }

            for (Integer integer : integers) {
                st = connection.prepareStatement("SELECT * FROM posts where pid=?");
                st.setInt(1, integer);
                resultSet = st.executeQuery();
                while (resultSet.next()) {
                    int pid = resultSet.getInt("pid");
                    ArrayList<Integer> members = getMembers(pid);
                    Post post = new Post(pid, resultSet.getInt("uid"),
                            resultSet.getString("name"), resultSet.getString("age"),
                            resultSet.getString("p_date"), resultSet.getString("location"),
                            resultSet.getString("level"), resultSet.getString("info"), members, getLikesCount(pid));
                    post.setMember(members.contains(uid));
                    posts.add(post);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        close();
        return posts;
    }

    public ArrayList<Integer> getMembers(int pid) {
        ArrayList<Integer> members = new ArrayList<>();

        try {
            PreparedStatement st = connection.prepareStatement("SELECT * FROM members where pid=?");
            st.setInt(1, pid);
            ResultSet resultSet = st.executeQuery();
            while (resultSet.next()) {
                members.add(resultSet.getInt("uid"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    public ArrayList<User> getMembersUsers(int pid) {
        ArrayList<User> users = new ArrayList<>();

        try {
            PreparedStatement st = connection.prepareStatement("SELECT * FROM members where pid=?");
            st.setInt(1, pid);
            ResultSet resultSet = st.executeQuery();
            while (resultSet.next()) {
                users.add(getUser(resultSet.getInt("uid")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        close();
        return users;
    }

    public DBHelper addMember(int pid, int uid) {
        ArrayList<Integer> members = getMembers(pid);
        members.add(uid);

        try {
            PreparedStatement st = connection.prepareStatement("INSERT INTO members (uid, pid) values (?,?);");
            st.setInt(1, uid);
            st.setInt(2, pid);
            st.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return this;
    }

    public int changeLike(int pid, int uid) {
        try {
            PreparedStatement st = connection.prepareStatement("SELECT * FROM likes WHERE uid = ? and pid = ?;");
            st.setInt(1, uid);
            st.setInt(2, pid);
            ResultSet resultSet = st.executeQuery();
            boolean isLike = resultSet.next();
            if (isLike) {
                removeLike(pid, uid);
            } else {
                st = connection.prepareStatement("INSERT INTO likes (uid, pid) values (?,?);");
                st.setInt(1, uid);
                st.setInt(2, pid);
                st.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return getLikesCount(pid);
    }

    private int getLikesCount(int pid) {
        int k = 0;
        try {
            PreparedStatement st = connection.prepareStatement("SELECT * FROM likes WHERE pid = ?;");
            st.setInt(1, pid);
            ResultSet resultSet = st.executeQuery();
            while (resultSet.next()) {
                k++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return k;
    }

    private DBHelper removeLike(int pid, int uid) {
        try {
            PreparedStatement st = connection.prepareStatement("DELETE FROM likes WHERE uid = ? and pid = ?;");
            st.setInt(1, uid);
            st.setInt(2, pid);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return this;
    }


    public int addBlogPost(BlogPost post) {
        try {
            PreparedStatement st = connection.prepareStatement("INSERT INTO blog_posts (uid, text_com, title, photo_post) values(?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            int k = 1;
            st.setInt(k++, post.getUid());
            st.setString(k++, post.getBody());
            st.setString(k++, post.getTitle());
            st.setString(k++, "");
            st.executeUpdate();
            ResultSet generatedKeys = st.getGeneratedKeys();
            generatedKeys.next();

            return generatedKeys.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    DBHelper updateBlogPost(BlogPost blogPost) {
        try {
            PreparedStatement st = connection.prepareStatement("UPDATE blog_posts set uid = ?, text_com= ?, title= ?, photo_post= ? WHERE id = ?;");
            int k = 1;
            st.setInt(k++, blogPost.getUid());
            st.setString(k++, blogPost.getBody());
            st.setString(k++, blogPost.getTitle());
            st.setString(k++, blogPost.getImage());
            st.setInt(k++, blogPost.getPid());

            st.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;

    }

    public ArrayList<BlogPost> getAllBlogPosts(int uid) {
        try {
            PreparedStatement st = connection.prepareStatement("SELECT * FROM blog_posts where uid = ?;");
            st.setInt(1, uid);
            ResultSet resultSet = st.executeQuery();
            ArrayList<BlogPost> posts = new ArrayList<>();

            while (resultSet.next()) {
                int pid = resultSet.getInt("id");
                BlogPost post = new BlogPost(resultSet.getInt("uid"), pid,
                        resultSet.getString("title"), resultSet.getString("text_com"),
                        resultSet.getString("photo_post"), getBlogPostLikesCount(pid));
                posts.add(post);
            }
            close();
            return posts;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        close();
        return new ArrayList<>();
    }

    public BlogPost geBlogPost(int pid) {
        try {
            PreparedStatement st = connection.prepareStatement("SELECT * FROM blog_posts where id = ?;");
            st.setInt(1, pid);
            ResultSet resultSet = st.executeQuery();

            resultSet.next();

            return new BlogPost(resultSet.getInt("uid"), pid,
                    resultSet.getString("title"), resultSet.getString("text_com"),
                    resultSet.getString("photo_post"), getBlogPostLikesCount(pid));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int changeBlogPostLike(int pid, int uid) {
        try {
            PreparedStatement st = connection.prepareStatement("SELECT * FROM likes_blog WHERE uid = ? and pid = ?;");
            st.setInt(1, uid);
            st.setInt(2, pid);
            ResultSet resultSet = st.executeQuery();
            boolean isLike = resultSet.next();
            if (isLike) {
                removeBlogPostLike(pid, uid);
            } else {
                st = connection.prepareStatement("INSERT INTO likes_blog (uid, pid) values (?,?);");
                st.setInt(1, uid);
                st.setInt(2, pid);
                st.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return getBlogPostLikesCount(pid);
    }

    private int getBlogPostLikesCount(int pid) {
        int k = 0;
        try {
            PreparedStatement st = connection.prepareStatement("SELECT * FROM likes_blog WHERE pid = ?;");
            st.setInt(1, pid);
            ResultSet resultSet = st.executeQuery();
            while (resultSet.next()) {
                k++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return k;
    }

    private DBHelper removeBlogPostLike(int pid, int uid) {
        try {
            PreparedStatement st = connection.prepareStatement("DELETE FROM likes_blog WHERE uid = ? and pid = ?;");
            st.setInt(1, uid);
            st.setInt(2, pid);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return this;
    }

    public HashMap<String, Object> getBlogPostComments(int pid, int my_uid) {
        ArrayList<BlogComment> blogComments = new ArrayList<>();
        HashMap<String, Object> response = new HashMap<>();

        try {
            BlogPost blogPost = geBlogPost(pid);
            String title = blogPost.getTitle();
            PreparedStatement st = connection.prepareStatement("SELECT * FROM comments_blog WHERE pid = ?;");
            st.setInt(1, pid);
            ResultSet resultSet = st.executeQuery();
            while (resultSet.next()) {
                String date = resultSet.getString("time_send");
                String text = resultSet.getString("text_com");
                int uid = resultSet.getInt("uid");
                User user = getUser(uid);
                user.setUserInfo(null);
                user.setBirthday(null);
                user.setPassworduser(null);
                user.setEmailuser(null);
                blogComments.add(new BlogComment(resultSet.getInt("id"), pid, uid, text, date, user, blogPost, my_uid));
            }
            response.put("title", title);
            response.put("comments", blogComments);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return response;
    }

    public DBHelper addBlogPostComments(int pid, int uid, String text) {
        try {
            PreparedStatement st = connection.prepareStatement("INSERT INTO comments_blog (uid, pid, time_send, text_com) values (?,?,?,?);");
            st.setInt(1, uid);
            st.setInt(2, pid);
            st.setString(3, new java.util.Date().toString());
            st.setString(4, text);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return this;
    }


    public HashMap<String, Object> getPostComments(int pid, int my_uid) {
        ArrayList<BlogComment> blogComments = new ArrayList<>();
        HashMap<String, Object> response = new HashMap<>();

        try {
            BlogPost blogPost = geBlogPost(pid);
            String title = blogPost.getTitle();
            PreparedStatement st = connection.prepareStatement("SELECT * FROM comments WHERE pid = ?;");
            st.setInt(1, pid);
            ResultSet resultSet = st.executeQuery();
            while (resultSet.next()) {
                String date = resultSet.getString("time_send");
                String text = resultSet.getString("text_com");
                int uid = resultSet.getInt("uid");
                User user = getUser(uid);
                user.setUserInfo(null);
                user.setBirthday(null);
                user.setPassworduser(null);
                user.setEmailuser(null);
                blogComments.add(new BlogComment(resultSet.getInt("id"), pid, uid, text, date, user, blogPost, my_uid));
            }
            response.put("title", title);
            response.put("comments", blogComments);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return response;
    }

    public DBHelper addPostComments(int pid, int uid, String text) {
        try {
            PreparedStatement st = connection.prepareStatement("INSERT INTO comments (uid, pid, time_send, text_com) values (?,?,?,?);");
            st.setInt(1, uid);
            st.setInt(2, pid);
            st.setString(3, new java.util.Date().toString());
            st.setString(4, text);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return this;
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
