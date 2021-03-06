package com.javier.edukka.service;

import com.javier.edukka.model.ActivityModel;
import com.javier.edukka.model.ClassModel;
import com.javier.edukka.model.GameModel;
import com.javier.edukka.model.QuizModel;
import com.javier.edukka.model.UserModel;
import com.javier.edukka.model.MultiplayerGameModel;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface RestInterface {

// User Service

    @GET("users")
    Call<List<UserModel>> getAllUsers();

    @GET("user/{id}")
    Call<UserModel> getUser(@Path("id") int userId);

    @GET("user/activity/{id}")
    Call<List<ActivityModel>> getUserActivity(@Path("id") int studentId);

    @FormUrlEncoded
    @POST("login")
    Call<UserModel> logIn(@Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("signup")
    Call<UserModel> signUp(@Field("name") String name, @Field("username") String username, @Field("password") String password,
                           @Field("role") String role, @Field("image") String image, @Field("class_id") int classId);

    @FormUrlEncoded
    @POST("user/score")
    Call<UserModel> updateUserScore(@Field("score") int score, @Field("id") int userId);

    @FormUrlEncoded
    @POST("user/edit")
    Call<UserModel> updateUser(@Field("name") String name, @Field("username") String username, @Field("password") String password,
                               @Field("image") String image, @Field("id") int userId);

    @FormUrlEncoded
    @POST("user/delete")
    Call<Void> deleteUser(@Field("id") int userId, @Field("role") String role);

// Class Service

    @GET("classes")
    Call<List<ClassModel>> getAllClasses();

    @GET("class/{id}")
    Call<ClassModel> getClass(@Path("id") int classId);

    @GET("myclass/{id}")
    Call<List<UserModel>> getUserClass(@Path("id") int classId);

    @GET("class/activity/{id}")
    Call<List<ActivityModel>> getClassActivity(@Path("id") int classId);

    @FormUrlEncoded
    @POST("class/new")
    Call<ClassModel> createClass(@Field("name") String name, @Field("information") String information, @Field("teacher_id") int teacherId);

    @FormUrlEncoded
    @POST("class/edit")
    Call<ClassModel> updateClass(@Field("name") String name, @Field("information") String information, @Field("id") int classId);

    @FormUrlEncoded
    @POST("class/delete")
    Call<Void> deleteClass(@Field("id") int classId);

    @FormUrlEncoded
    @POST("class/adduser")
    Call<ClassModel> addUserClass(@Field("id") int userId, @Field("class_id") int classId);

    @FormUrlEncoded
    @POST("class/remuser")
    Call<Void> removeUserClass(@Field("id") int userId);

// Game Service

    @GET("games")
    Call<List<GameModel>> getAllGames();

    @GET("game/{id}")
    Call<GameModel> getGame(@Path("id") int gameId);

    @GET("games/{sub}")
    Call<List<GameModel>> getSubjectGames(@Path("sub") String subject);

    @FormUrlEncoded
    @POST("game/new")
    Call<GameModel> createGame(@Field("subject") String subject, @Field("title") String title, @Field("description") String description,
                               @Field("locale") String locale, @Field("difficulty") String difficulty, @Field("time") String time, @Field("classId") String classId);

    @FormUrlEncoded
    @POST("game/edit")
    Call<GameModel> updateGame(@Field("title") String title, @Field("description") String description, @Field("difficulty") String difficulty,  @Field("time") String time, @Field("visibility") String visibility, @Field("id") String gameId);

    @FormUrlEncoded
    @POST("game/delete")
    Call<Void> deleteGame(@Field("id") int gameId);

    @FormUrlEncoded
    @POST("game/finish")
    Call<ActivityModel> finishGame(@Field("student_id") int studentId, @Field("game_id") int gameId, @Field("subject") String subject, @Field("result") float result);

    @FormUrlEncoded
    @POST("game/upvote")
    Call<GameModel> upvoteGame(@Field("id") int gameId);

    @FormUrlEncoded
    @POST("game/downvote")
    Call<GameModel> downvoteGame(@Field("id") int gameId);

// Quiz Service

    @GET("quizzes")
    Call<List<QuizModel>> getAllQuizzes();

    @GET("quiz/{id}")
    Call<QuizModel> getQuiz(@Path("id") int quizId);

    @GET("play/{id}")
    Call<List<QuizModel>> getGameQuiz(@Path("id") int gameId);

    @FormUrlEncoded
    @POST("quiz/new")
    Call<QuizModel> createQuiz(@Field("question") String question, @Field("answer") String answer, @Field("options") String options, @Field("type") String type, @Field("game_id") int gameId);

    @FormUrlEncoded
    @POST("quiz/edit")
    Call<QuizModel> updateQuiz(@Field("question") String question, @Field("answer") String answer, @Field("options") String options, @Field("id") int quizId);

    @FormUrlEncoded
    @POST("quiz/delete")
    Call<Void> deleteQuiz(@Field("id") int quizId);

// Multiplayer Game Service

    @GET("room/{id}")
    Call<MultiplayerGameModel> getRoom(@Path("id") int roomId);

    @GET("rooms/{id}")
    Call<List<MultiplayerGameModel>> searchRoom(@Path("id") int classId);

    @GET("room/randomquizzes/{id}")
    Call<List<QuizModel>> getRandomQuizzes(@Path("id") int classId);

    @FormUrlEncoded
    @POST("room/new")
    Call<MultiplayerGameModel> createRoom(@Field("user1") int user1, @Field("id_user1") String id_user1, @Field("user2") int user2, @Field("id_user2") String id_user2, @Field("quizzes") String quizzes,
                                          @Field("status") String status, @Field("class_id") int class_id, @Field("extra") int extra, @Field("data") String data);

    @FormUrlEncoded
    @POST("room/join")
    Call<MultiplayerGameModel> joinRoom(@Field("user2") int user2, @Field("id_user2") String id_user2, @Field("status") String status, @Field("id") int id);

    @FormUrlEncoded
    @POST("room/leave")
    Call<Void> leaveRoom(@Field("id") int id);

    @FormUrlEncoded
    @POST("room/adorn")
    Call<Void> adornRoom(@Field("id") int id);

    @FormUrlEncoded
    @POST("room/delete")
    Call<Void> deleteRoom(@Field("id") int roomId);

    @FormUrlEncoded
    @POST("room/updatequizzes")
    Call<Void> updateQuizzes(@Field("quizzes") String quizzes, @Field("id") int id);

    @FormUrlEncoded
    @POST("room/getquizzesbyid")
    Call<List<QuizModel>> getQuizzesById(@Field("id1") int id1, @Field("id2") int id2, @Field("id3") int id3, @Field("id4") int id4, @Field("id5") int id5);


// Firebase Messaging

    @FormUrlEncoded
    @POST("/edukka/firebase/send.php")
    Call<Void> sendMessage(@Field("firebase_id") String firebaseId, @Field("message") String message);


// Upload Service

    @Multipart
    @POST("/edukka/images/index.php")
    Call<UploadObject> postImage(@Part MultipartBody.Part image, @Part("name") RequestBody name);

    @Multipart
    @POST("/edukka/sounds/index.php")
    Call<UploadObject> postSound(@Part MultipartBody.Part sound, @Part("name") RequestBody name);
}
