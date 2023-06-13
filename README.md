# Data Spatial Hybrid Grid Maps
This code is dependent on GeometryCore: https://github.com/tue-alga/GeometryCore.
The code complements the paper: Data-spatial Layouts for Grid Maps.

To make images: 
1. Load one of the datasets (e.g. click 'Open France map and data')
2. Click 'Create grid'.
3. Click 'Assign to grid (spatial)'. This computes the spatial assignment using an LP and is needed to compute the outline in which the algorithm will work.
4. (Optional) to create a data assignment click 'Random Simulated Annealing'
5. Input some Spatial / Data slack and click 'Spatial -> Data' or 'Data -> Spatial'. 

All results from the paper have been generated with this code. Randomized results use the seeds as found in the code.
Results with increasing slack have been generated incrementally.
For example, to get a slack with one, we input slack 1. To get a slack of 2, we input slack one and start from the previous configuration.


# Hierarchical Grid Maps
At the top there is a tab with 'Hierarchical'.
Press 'Do all UK / NL'.
Now, the higher level is determined by the radio buttons, and the lower level by the 'Assign to grid' buttons.
Note: to achieve a data lower level layout, first a spatial lower level layout must be computed to get an outline.