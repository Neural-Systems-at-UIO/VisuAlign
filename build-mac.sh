javac -d bin --module-path javafx-jmods --source-path . visualign/QNonLin.java
cp visualign/QNonLin.fxml bin/visualign
jlink --module-path bin:javafx-jmods --add-modules qnonlin --output VisuAlign-mac --compress=2
cp launchers/launcher-mac.txt VisuAlign-mac/VisuAlign
chmod a+x VisuAlign-mac/VisuAlign
cp -R cutlas/*.cutlas VisuAlign-mac

# Renaming VisuAlign-mac to VisuAlign.app makes it an application, but for some reason it refuses to load atlases then.
