@dir javafx-jmods\*.jmod >nul 2>nul
@if errorlevel 1 goto :nojmods
@rd /s/q bin
javac -d bin --module-path javafx-jmods --source-path . visualign/QNonLin.java
@if errorlevel 1 goto :exit
copy visualign\QNonLin.fxml bin\visualign
@if errorlevel 1 goto :exit
jlink --module-path bin;javafx-jmods --add-modules qnonlin --output VisuAlign-win --compress=2
@if errorlevel 1 goto :exit
copy launchers\launcher-win.txt VisuAlign-win\VisuAlign.bat
@if errorlevel 1 goto :exit
@dir cutlas\*.cutlas >nul 2>nul
@if errorlevel 1 goto :nocutlas
xcopy /s cutlas\*.* VisuAlign-win
@goto :exit
:nojmods
@echo ERROR: javafx-jmods folder contains no JavaFX modules, compilation can't proceed
@goto :exit
:nocutlas
@echo WARNING: cutlas folder contains no atlases (you can copy them manually into the VisuAlign folder)
@goto :exit
:exit
