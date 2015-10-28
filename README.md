#stu-json-to-object Parser

Is a json parser that eliminates the use of creating your own parser class(that will convert JSON representation to Java Object)
that could take time depending on parsing complexity. This java library was designed to make [JSON](http://www.json.org/)
parsing as easy and quick as possible.

#Features
This library will handle the [JSON](http://www.json.org/) parsing into Java Object. All you need to do is to add a few lines of code.

#Implementation
##Adding the as module
Clone this repo and import it to your current Android project. On android studio, go to File>New>Import Module and find the directory
to where you clone the repository. Click finish confirm the import.

Once done importing, add this to your `build.gradle` file dependencies if not created:
`compile project(':stu-json-to-object')`

For example:
```
dependencies {
    compile project(':stu-json-to-object')
}
```

##Implement to the project

Example json object response for an API request:
```
//jsonResponse
{
    "user":{
        "id": 12345,
        "first_name": "Jhane",
        "last_name": "Reyes",
        "age": 40,
        "gender": "female",
    }
}
```

Setting up the Model Object:
Give a setter spec to the setter methods of the fields and then put the json keys to the setter spec.
```
public class User {
    private int mId;
    private String mFirstName;
    private String mLastName;
    private int mAge;
    private String mGender;

    //Setters
    @SetterSpec(jsonKey = "id")
    public void setId(int id) {
        mId = id;
    }

    @SetterSpec(jsonKey = "first_name")
    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    @SetterSpec(jsonKey = "last_name")
    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    @SetterSpec(jsonKey = "age")
    public void setAge(int age) {
        mAge = age;
    }

    @SetterSpec(jsonKey = "gender")
    public void setGender(String gender) {
        mGender = gender;
    }

    //Getters
    public int getId() {
        return mId;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public int getAge() {
        return mAge;
    }

    public String getGender() {
        return mGender;
    }
}
```
Note: The setter spec will serve as a linker of the setter method and the value gathered from the JSON response using the json key.
The json key that will be set to the SetterSpec should match to the JSON response attributes, if not the value of the field will be null.

Using the parser:
```
// Parse the json object
User user = ModelParser.parse(User.class, jsonResponse);

// Test the results
Log.i(TAG, "ID: " + user.getId());
Log.i(TAG, "FirstName: " + user.getFirstName());
Log.i(TAG, "LastName: " + user.getLastName());
Log.i(TAG, "Age: " + user.getAge());
Log.i(TAG, "Gender: " + user.getGender());
```

And that would be it.