package com.rfr;

import com.google.gson.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.ApplicationScope;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

@Controller
@ApplicationScope
@CrossOrigin
@RequestMapping(value = "/api/*")
public class MainController {
    private final static String error_f = "{\"error\":\"%s\"}";
    private final static String response_f = "{\"response\":\"%s\"}";


    /**
     * POST http://examle.org:8080/api/registration
     * <p>
     * Регистрация пользователя
     *
     * @param firstName    - имя
     * @param lastName     - фамилия
     * @param middleName   - отчетсво
     * @param birthday     - дата рождения
     * @param passworduser - пароль
     * @param emailuser    - мейл
     * @return
     */
    @PostMapping(value = "registration", produces = "application/json")
    public ResponseEntity<String> registration(@RequestParam(name = "firstName", defaultValue = "") String firstName,
                                               @RequestParam(name = "lastName", defaultValue = "") String lastName,
                                               @RequestParam(name = "middleName", defaultValue = "") String middleName,
                                               @RequestParam(name = "birthday", defaultValue = "") String birthday,
                                               @RequestParam(name = "passworduser", defaultValue = "") String passworduser,
                                               @RequestParam(name = "emailuser", defaultValue = "") String emailuser) {
        if (!firstName.isEmpty() && !lastName.isEmpty() && !middleName.isEmpty() && !birthday.isEmpty() && !passworduser.isEmpty() && !emailuser.isEmpty()) {
            try {
                new DBHelper().addUser(new User(firstName, lastName, middleName, birthday, CodingUtils.encode(passworduser), emailuser)).close();
            } catch (Exception e) {
                return new ResponseEntity<>(String.format(error_f, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", "/");
            return new ResponseEntity<>(headers, HttpStatus.FOUND);

        }
        return new ResponseEntity<>(String.format(error_f, "Param can't be null!"), HttpStatus.BAD_REQUEST);
    }

    /**
     * POST http://examle.org:8080/api/login
     * <p>
     * Авторизация пользователя
     *
     * @param emailuser    - мейл
     * @param passworduser - пароль
     * @return
     */
    @PostMapping(value = "login", produces = "application/json")
    public ResponseEntity<String> login(@RequestParam(name = "emailuser", defaultValue = "") String emailuser,
                                        @RequestParam(name = "passworduser", defaultValue = "") String passworduser) {

        if (!passworduser.isEmpty() && !emailuser.isEmpty()) {
            try {
                DBHelper dbHelper = new DBHelper();
                User user = dbHelper.getUser(emailuser);
                dbHelper.close();
                if (user == null) {
                    return new ResponseEntity<>(String.format(error_f, "User is not registered!"), HttpStatus.BAD_REQUEST);
                }
                if (user.getPassworduser().equals(CodingUtils.encode(passworduser))) {
                    return new ResponseEntity<>(String.format(response_f, CodingUtils.tokenGenerator(emailuser)), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(String.format(error_f, "Incorrect password"), HttpStatus.BAD_REQUEST);
                }
            } catch (Exception e) {
                return new ResponseEntity<>(String.format(error_f, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }
        return new ResponseEntity<>(String.format(error_f, "Param can't be null!"), HttpStatus.BAD_REQUEST);
    }


    /**
     * GET http://examle.org:8080/api/checkToken
     * <p>
     * Метод позволяет проверить токен на валидность.
     * В случае успешной проверки, возвращает email пользователя данного токена
     *
     * @param token
     * @return
     */
    @GetMapping(value = "checkToken", produces = "application/json")
    public ResponseEntity<String> checkToken(@RequestParam(name = "token") String token) {
        return new ResponseEntity<>(String.format(response_f, CodingUtils.getTokenOwner(token)), HttpStatus.OK);
    }

    /**
     * GET http://examle.org:8080/api/getUserById/{id}
     * <p>
     * Метод позволяет получить данные пользователя в формате json
     *
     * @param id - id пользователя
     * @return
     */
    @GetMapping(value = "getUserById/{id}", produces = "application/json")
    public ResponseEntity<String> getUserById(@PathVariable(name = "id") int id) {

        DBHelper dbHelper = new DBHelper();
        User user = dbHelper.getUser(id);
        dbHelper.close();
        if (user != null)
            return new ResponseEntity<>(new GsonBuilder().setPrettyPrinting().create().toJson(user), HttpStatus.OK);

        return new ResponseEntity<>(String.format(error_f, "No user!"), HttpStatus.BAD_REQUEST);
    }

    /**
     * GET http://examle.org:8080/api/getUserById/{id}
     * <p>
     * Метод позволяет получить данные пользователя в формате json
     *
     * @param token - токен пользователя
     * @return
     */
    @GetMapping(value = "getUser", produces = "application/json")
    public ResponseEntity<String> getUserById(@RequestParam(name = "token") String token, @RequestParam(name = "uid", defaultValue = "-1") int uid) {
        User user;
        DBHelper dbHelper = new DBHelper();
        if(uid == -1){
            String tokenOwner = CodingUtils.getTokenOwner(token);
            user = dbHelper.getUser(tokenOwner);
        }else {
            user = dbHelper.getUser(uid);
        }
        dbHelper.close();
        if (user != null)
            return new ResponseEntity<>(new GsonBuilder().setPrettyPrinting().create().toJson(user), HttpStatus.OK);

        return new ResponseEntity<>(String.format(error_f, "No user!"), HttpStatus.BAD_REQUEST);
    }


    /**
     * PUT http://examle.org:8080/api/uploadAvatar
     * <p>
     * Метод позволяет загрузить новый аватр пользователя
     *
     * @param body - json, в котором содержится токен пользователя и массив байтов - изображение
     *             принимает формат: {"token":"usertoken", "formData": [1,2,3]}
     * @return
     */
    @PutMapping(value = "uploadAvatar", produces = "application/json")
    public ResponseEntity<String> uploadAvatar(@RequestBody String body) {
        try {
            Gson gson = new Gson();
            JsonObject object = gson.fromJson(body, JsonObject.class);
            JsonArray formData = object.get("formData").getAsJsonArray();

            byte[] file = new byte[formData.size()];
            int i = 0;
            for (JsonElement formDatum : formData) {
                file[i++] = formDatum.getAsByte();
            }
            String token = object.get("token").getAsString();

            String tokenOwner = CodingUtils.getTokenOwner(token);
            User user = new DBHelper().getUser(tokenOwner);
            Integer id = user.getId();
            user.setAvatar(Constants.PICTURES_URL_LOCATION + id + "/avatar.png");
            new DBHelper().updateUser(user).close();
            String s = Constants.PICTURES_FILE_LOCATIONS + id + "/";
            File file1 = new File(s);
            file1.mkdirs();
            FileOutputStream stream = new FileOutputStream(s + "avatar.png");
            stream.write(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(String.format(error_f, "Uploaded!"), HttpStatus.OK);
    }


    @PostMapping(value = "setUserInfo", produces = "application/json")
    public ResponseEntity<String> setUserInfo(@RequestParam(name = "token", defaultValue = "") String token,
                                              @RequestParam(name = "city", defaultValue = "") String city,
                                              @RequestParam(name = "school", defaultValue = "") String school,
                                              @RequestParam(name = "rang", defaultValue = "") String rang,
                                              @RequestParam(name = "trener", defaultValue = "") String trener,
                                              @RequestParam(name = "about", defaultValue = "") String about) {

        if (!token.isEmpty() && !city.isEmpty() && !school.isEmpty() && !rang.isEmpty() && !trener.isEmpty() && !about.isEmpty()) {
            String tokenOwner = CodingUtils.getTokenOwner(token);
            Integer id = new DBHelper().getUser(tokenOwner).getId();
            new DBHelper().setUserInfo(id, new UserInfo(city, school, rang, trener, about)).close();

            return new ResponseEntity<>(String.format(response_f, "zaebis"), HttpStatus.OK);
        }
        return new ResponseEntity<>(String.format(error_f, "Param can't be null!"), HttpStatus.BAD_REQUEST);

    }

    @PostMapping(value = "addPost", produces = "application/json")
    public ResponseEntity<String> addPost(@RequestParam(name = "token", defaultValue = "") String token,
                                          @RequestParam(name = "name", defaultValue = "") String name,
                                          @RequestParam(name = "age", defaultValue = "") String age,
                                          @RequestParam(name = "date", defaultValue = "") String date,
                                          @RequestParam(name = "location", defaultValue = "") String location,
                                          @RequestParam(name = "level", defaultValue = "") String level,
                                          @RequestParam(name = "info", defaultValue = "") String info) {

        if (!token.isEmpty() && !name.isEmpty() && !age.isEmpty() && !date.isEmpty() && !location.isEmpty() && !level.isEmpty() && !info.isEmpty()) {
            String tokenOwner = CodingUtils.getTokenOwner(token);
            Integer id = new DBHelper().getUser(tokenOwner).getId();
            new DBHelper().addPost(new Post(id, name, age, date, location, level, info)).close();

            return new ResponseEntity<>(String.format(response_f, "zaebis"), HttpStatus.OK);
        }
        return new ResponseEntity<>(String.format(error_f, "Param can't be null!"), HttpStatus.BAD_REQUEST);

    }

    @PostMapping(value = "getAllPosts", produces = "application/json")
    public ResponseEntity<String> getAllPosts(@RequestParam(name = "token") String token) {
        String tokenOwner = CodingUtils.getTokenOwner(token);
        DBHelper dbHelper = new DBHelper();
        Integer uid = dbHelper.getUser(tokenOwner).getId();
        String s = new GsonBuilder().setPrettyPrinting().create().toJson(dbHelper.getAllPosts(uid));
        dbHelper.close();
        return new ResponseEntity<>(s, HttpStatus.OK);
    }

    @PostMapping(value = "getPosts", produces = "application/json")
    public ResponseEntity<String> getPosts(@RequestParam(name = "token") String token, @RequestParam(name = "uid", defaultValue = "-1") int uid) {
        DBHelper dbHelper = new DBHelper();

        if(uid == -1){
            String tokenOwner = CodingUtils.getTokenOwner(token);
            uid = dbHelper.getUser(tokenOwner).getId();
        }

        String s = new GsonBuilder().setPrettyPrinting().create().toJson(dbHelper.getPosts(uid));
        dbHelper.close();
        return new ResponseEntity<>(s, HttpStatus.OK);
    }

    @PostMapping(value = "addMember", produces = "application/json")
    public ResponseEntity<String> addMember(@RequestParam(name = "token") String token, @RequestParam(name = "pid") int pid) {
        String tokenOwner = CodingUtils.getTokenOwner(token);
        DBHelper dbHelper = new DBHelper();
        Integer uid = dbHelper.getUser(tokenOwner).getId();
        dbHelper.addMember(pid, uid).close();
        return new ResponseEntity<>(String.format(response_f, "ok"), HttpStatus.OK);
    }

    @PostMapping(value = "getMemberPosts", produces = "application/json")
    public ResponseEntity<String> getMemberPosts(@RequestParam(name = "token") String token) {
        String tokenOwner = CodingUtils.getTokenOwner(token);
        DBHelper dbHelper = new DBHelper();
        Integer uid = dbHelper.getUser(tokenOwner).getId();
        String s = new GsonBuilder().setPrettyPrinting().create().toJson(dbHelper.getPostIfMember(uid));
        dbHelper.close();
        return new ResponseEntity<>(s, HttpStatus.OK);
    }

    @GetMapping(value = "getPostMembers", produces = "application/json")
    public ResponseEntity<String> getPostMembers(@RequestParam(name = "pid") int pid) {
        DBHelper dbHelper = new DBHelper();
        String s = new GsonBuilder().setPrettyPrinting().create().toJson(dbHelper.getMembersUsers(pid));
        dbHelper.close();
        return new ResponseEntity<>(s, HttpStatus.OK);
    }

    @PostMapping(value = "changeLikeToPost", produces = "application/json")
    public ResponseEntity<String> changeLikeToPost(@RequestParam(name = "token") String token,
                                                   @RequestParam(name = "pid") int pid) {
        String tokenOwner = CodingUtils.getTokenOwner(token);
        DBHelper dbHelper = new DBHelper();
        Integer uid = dbHelper.getUser(tokenOwner).getId();
        int k = dbHelper.changeLike(pid, uid);
        dbHelper.close();
        return new ResponseEntity<>(String.format(response_f, k), HttpStatus.OK);
    }

    @PutMapping(value = "addBlogPost", produces = "application/json")
    public ResponseEntity<String> addBlogPost(@RequestBody String body) {
        try {
            DBHelper dbHelper = new DBHelper();

            Gson gson = new Gson();
            JsonObject object = gson.fromJson(body, JsonObject.class);
            JsonArray formData = object.get("formData").getAsJsonArray();
            byte[] file = new byte[formData.size()];
            int i = 0;
            for (JsonElement formDatum : formData) file[i++] = formDatum.getAsByte();
            String token = object.get("token").getAsString();
            String title = object.get("title").getAsString();
            String post_body = object.get("post_body").getAsString();


            String tokenOwner = CodingUtils.getTokenOwner(token);
            User user = dbHelper.getUser(tokenOwner);
            Integer id = user.getId();
            BlogPost blogPost = new BlogPost(id, title, post_body);
            int pid = dbHelper.addBlogPost(blogPost);
            blogPost.setPid(pid);

            blogPost.setImage(Constants.PICTURES_URL_LOCATION + id + "/" + pid + ".png");
            dbHelper.updateBlogPost(blogPost).close();

            String s = Constants.PICTURES_FILE_LOCATIONS + id + "/";
            File file1 = new File(s);
            file1.mkdirs();
            FileOutputStream stream = new FileOutputStream(s + pid + ".png");
            stream.write(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(String.format(error_f, "Uploaded!"), HttpStatus.OK);
    }

    @PostMapping(value = "getAllBlogPosts", produces = "application/json")
    public ResponseEntity<String> getAllBlogPosts(@RequestParam(name = "token") String token) {
        String tokenOwner = CodingUtils.getTokenOwner(token);
        DBHelper dbHelper = new DBHelper();
        Integer uid = dbHelper.getUser(tokenOwner).getId();
        String s = new GsonBuilder().setPrettyPrinting().create().toJson(dbHelper.getAllBlogPosts(uid));
        dbHelper.close();
        return new ResponseEntity<>(s, HttpStatus.OK);
    }

    @PostMapping(value = "changeLikeToBlogPost", produces = "application/json")
    public ResponseEntity<String> changeLikeToBlogPost(@RequestParam(name = "token") String token,
                                                   @RequestParam(name = "pid") int pid) {
        String tokenOwner = CodingUtils.getTokenOwner(token);
        DBHelper dbHelper = new DBHelper();
        Integer uid = dbHelper.getUser(tokenOwner).getId();
        int k = dbHelper.changeBlogPostLike(pid, uid);
        dbHelper.close();
        return new ResponseEntity<>(String.format(response_f, k), HttpStatus.OK);
    }

    @PostMapping(value = "getBlogPostComments", produces = "application/json")
    public ResponseEntity<String> getBlogPostComments(@RequestParam(name = "token") String token,
                                                      @RequestParam(name = "pid") int pid) {

        String tokenOwner = CodingUtils.getTokenOwner(token);
        DBHelper dbHelper = new DBHelper();
        Integer my_uid = dbHelper.getUser(tokenOwner).getId();
        String s = new GsonBuilder().setPrettyPrinting().create().toJson(dbHelper.getBlogPostComments(pid, my_uid));
        dbHelper.close();

        return new ResponseEntity<>(s, HttpStatus.OK);
    }

    @PostMapping(value = "addBlogPostComments", produces = "application/json")
    public ResponseEntity<String> addBlogPostComments(@RequestParam(name = "token") String token,
                                                      @RequestParam(name = "pid") int pid,
                                                      @RequestParam(name = "text") String text) {
        String tokenOwner = CodingUtils.getTokenOwner(token);
        DBHelper dbHelper = new DBHelper();
        Integer uid = dbHelper.getUser(tokenOwner).getId();
        dbHelper.addBlogPostComments(pid, uid, text).close();

        return new ResponseEntity<>(String.format(response_f, "OK"), HttpStatus.OK);
    }


    @PostMapping(value = "getPostComments", produces = "application/json")
    public ResponseEntity<String> getPostComments(@RequestParam(name = "token") String token,
                                                      @RequestParam(name = "pid") int pid) {

        String tokenOwner = CodingUtils.getTokenOwner(token);
        DBHelper dbHelper = new DBHelper();
        Integer my_uid = dbHelper.getUser(tokenOwner).getId();
        String s = new GsonBuilder().setPrettyPrinting().create().toJson(dbHelper.getPostComments(pid, my_uid));
        dbHelper.close();

        return new ResponseEntity<>(s, HttpStatus.OK);
    }

    @PostMapping(value = "addPostComments", produces = "application/json")
    public ResponseEntity<String> addPostComments(@RequestParam(name = "token") String token,
                                                      @RequestParam(name = "pid") int pid,
                                                      @RequestParam(name = "text") String text) {
        String tokenOwner = CodingUtils.getTokenOwner(token);
        DBHelper dbHelper = new DBHelper();
        Integer uid = dbHelper.getUser(tokenOwner).getId();
        dbHelper.addPostComments(pid, uid, text).close();

        return new ResponseEntity<>(String.format(response_f, "OK"), HttpStatus.OK);
    }
}
