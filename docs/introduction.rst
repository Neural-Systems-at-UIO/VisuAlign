**What is VisuAlign?**
------------------- 
VisuAlign is one of several tools developed by the Human Brain Project
(HBP) with the aim of facilitating brain atlas based analysis and
integration of experimental data and knowledge about the human and
rodent brain. VisuAlign is a tool for applying user-guided nonlinear refinements (inplane) to an existing,
affine 2D-to-3D registration, such as created using QuickNII (https://quicknii.readthedocs.io).
While linear registration tools are vital in bringing experimental image data to standardized coordinate spaces,
for precise quantitative analysis one has to address the residual anatomical variability among test subjects after registration.
VisuAlign is a tool designed for this task. (https://ebrains.eu/service/quicknii-and-visualign).

.. tip:: 
   **QuickNII and VisuAlign are part of the QUINT workflow**
   
   Visit `EBRAINS <https://ebrains.eu/service/quint/>`_ for more information about QUINT. Find full `user documentation here <https://quint-workflow.readthedocs.io>`_. 
   
.. image:: a2e4586e8dc145d5bfdcaec7c21ac926/media/image6.png
      :width: 6.30139in
      :height: 3.40345in

**Which atlases are supported?**
-----------------------------
1. Allen Mouse Brain Atlas Common Coordinate Framework version 3 (2015 and 2017) (CCFv3) (Wang et al. 2020. Cell, https://doi.org/10.1016/j.cell.2020.04.007. Epub 2020 May 7; RRID:JCR_020999 and RRID:JRC_021000) 
2. Waxholm Atlas of the Sprague Dawley rat, version 3 and 4 (WHS rat brain atlas) (Osen et al. 2019. NeuroImage, https:doi.org/10.1016/j.neuroimage.2019.05.016; Kleven et al. Nat Methods, 2020. https://doi.org/10.1038/s41592-023-02034-3; RRID:SCR_017124)
3. Developmental Mouse Brain Atlas, version 2 (DeMBA) (Carey et al. 2024. Nat Comm. https://doi.org/10.1038/s41467-025-63177-9; RRID:SCR_025324).

We encourage researchers who use DeMBA to cite both the dataset and publication presenting DeMBA, and to specify the age of any template(s) used and the version of any segmentation(s) used.

-Allen CCFv3 segmentations: Wang et al. (2020). The Allen Mouse Brain Common Coordinate Framework: A 3D Reference Atlas. Cell. https://doi.org/10.1016/j.cell.2020.04.007.

-KimLabDev segmentations: Kronman et al. (2024). Developmental mouse brain common coordinate framework. Nature Communications. https://doi.org/10.1038/s41467-024-53254-w

**What is the output of VisuAlign?**
---------------------------------

-A registration file (JSON format) containing the coordinates of your regisered section images in atlas space. 
This file is used in the QUINT workflow for quantification of segmented objects from your section images.
