# interakt
Interactive prompts made easy!

## Install

### Gradle - Kotlin DSL
```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("xyz.davidsimon:interakt:0.1.0")
}
```

### Maven
```xml
<dependency>
    <groupId>xyz.davidsimon</groupId>
    <artifactId>interakt</artifactId>
    <version>0.2.0</version>
</dependency>
```

## Usage
![asciicast](media/demo.gif)

To construct and run an interactive prompt, you need to:
1. Obtain a `Prompt` instance
2. Add `PromptField`s to the prompt
3. Execute the prompt

Using a few convenience methods, we can achieve all this in just a few lines of code:
```kotlin
prompt {
    text("foo:", default = "default value")
    integer("bar:", default = 42)
    singleList("baz:", listOf("1", "2", "3"), true)
    list("multi:", listOf(
        ListField.Choice("one", 1),
        ListField.Choice("two", 2),
        ListField.Choice("three", 3)
    ))

    println("Result:")
    for((key, value) in execute()) {
        println("${key.promptMessage} $value")
    }
}
```

## Fields

### `PromptField`
Base class for all other fields  

**Options:**  

| Parameter     | Type                            | Description                                                                                                         |
|---------------|---------------------------------|---------------------------------------------------------------------------------------------------------------------|
| promptMessage | `String`                        | Message to display before user input                                                                                |
| shouldPrompt  | `(PromptResult, T?) -> Boolean` | Controls whether the field should be prompted. Receives the answers entered so far and the current field's instance |
| default       | `(PromptResult, T?) -> T?`      | Provides a default value that will be pre-filled                                                                    |

### `StatefulField`
Base class for fields that have a complex rendering function. Has event handlers that receive the current `RenderState`.

### `TextField`
Prompts user for simple text input

### `IntegerField`
Prompts user for integer input

### `ListField`
Prompts the user to choose items from a list. The displayed name and the actual value of the items may differ.

**Options:**

| Parameter | Type                                | Description                   |
|-----------|-------------------------------------|-------------------------------|
| choices   | `(PromptResult) -> List<Choice<T>>` | Returns the available choices |

### `SingleValueListField`
Prompts the user to choose **ONE** item from a list.

### `TextListField`
Prompts the user to choose from a list of text items. Optionally the user may enter a custom value.

**Options:**

| Parameter           | Type                                         | Description                                                                                                     |
|---------------------|----------------------------------------------|-----------------------------------------------------------------------------------------------------------------|
| choices             | `(tr: PromptResult) -> List<Choice<String>>` | Returns the available choices                                                                                   |
| allowCustom         | `Boolean`                                    | Allow the user to enter a custom value. If `true` an additional choice will be added with an empty string value |
| customPromptMessage | `String`                                     | Message to display when user enter custom value                                                                 |

## Acknowledgement
interakt is built using [JLine](https://github.com/jline/jline3)