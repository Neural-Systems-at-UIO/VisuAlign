**FAQ**
--------------------------------

(1) I  cannot open the JSON file I have created from QuicNII even having the json and the png files in the same folder. An error message is saying "Exception in thread "JavaFX Application Thread" java.lang.ArithmeticException: / by zero""

--> It’s likely some special character in the names (either the files or the folder). So far we have seen % and # causing this, but there can be others.
Removing special characters should fix the problem.
