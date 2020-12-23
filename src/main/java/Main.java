import DB.Connector;
import User.User;
import org.json.*;

import java.io.*;
import java.net.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;

public class Main {
    public static void main(String[] args) throws IOException, JSONException, SQLException {
        var groupName = "iritrtf_urfu";
        var token = "95b10a5e16213f4b8041462c19312c117c94bce53a10c2eddeead4f86c1f134f9312dd33a000d472ffd17";
        var fileName = "out2.json";
        var tableName = "Users";
        var response = getGroupMembersVKAPI(groupName, token); //ответ API
        var list = responseToList(response); // json -> Лист пользователей
        var jsonObj = listToJson(list); // обратно в json
        writeJson2File(jsonObj, fileName); // сохранение json в файл
        var readJson = readJsonFile(fileName); // чтение json из файла
        var connector = new Connector(tableName);
        connector.insertUsers(readJson); // добавление пользователей в DB
        list = connector.getUsers(); // получение пользователей из DB
        var array = list.stream().filter(x -> x.sex == 1).sorted(Comparator.comparing(x -> x.fullName)).toArray(); // Выбор пользователей женского пола
        for (var e : array)
            System.out.println(e);
    }

    private static JSONArray getGroupMembersVKAPI(String groupName, String token) throws IOException, JSONException {
        var stringUrl = String.format("https://api.vk.com/method/groups.getMembers" +
                        "?group_id=%s" +
                        "&fields=sex" +
                        "&access_token=%s" +
                        "&v=5.126",
                groupName,
                token);
        var urlConnection = new URL(stringUrl).openConnection();
        var buffer = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        var result = buffer.readLine();
        buffer.close();
        return new JSONObject(result).getJSONObject("response").getJSONArray("items");
    }

    private static ArrayList<User> responseToList(JSONArray jsonArr) {
        var l = new ArrayList<User>();
        for (var i = 0; i < jsonArr.length(); i++) {
            var user = jsonArr.getJSONObject(i);
            var full_name = user.getString("last_name") + " " + user.getString("first_name");
            l.add(new User(full_name, user.getInt("sex")));
        }
        return l;
    }

    private static JSONObject listToJson(ArrayList<User> list) {
        var json = new JSONObject();
        for (var e : list)
            json.put(e.fullName, e.sex);

        return json;
    }

    private static void writeJson2File(JSONObject jsonObj, String fileName) throws IOException {
        var path = Paths.get(fileName);
        try {
            Files.createFile(path);
        } catch (FileAlreadyExistsException e) {
            Files.delete(path);
            Files.createFile(path);
        }
        FileWriter fw = new FileWriter(fileName);
        fw.write(jsonObj.toString());
        fw.close();
    }

    private static JSONObject readJsonFile(String fileName) throws IOException {
        var reader = new BufferedReader(new FileReader(fileName));
        return new JSONObject(reader.readLine());
    }
}