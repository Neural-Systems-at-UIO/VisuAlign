Copy the ".cutlas" folders here you wish to use. VisuAlign needs them to contain ```labels.nii.gz``` and ```labels.txt``` files, like

    ABA_Mouse_CCFv3_2015_25um.cutlas
       |-- labels.nii.gz
       |-- labels.txt
    WHS_Rat_v4_39um.cutlas
       |-- labels.nii.gz
       |-- labels.txt
    ...

where ```labels.txt``` is an ITK-Snap label file.

VisuAlign can be built without atlas data, these folders are simply copied next to the app, and thus they can also be manually copied there later.