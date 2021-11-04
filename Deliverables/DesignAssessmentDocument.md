# Design assessment


```
<The goal of this document is to analyse the structure of your project, compare it with the design delivered
on April 30, discuss whether the design could be improved>
```

# Levelized structure map
```
<Applying Structure 101 to your project, version to be delivered on june 4, produce the Levelized structure map,
with all elements explosed, all dependencies, NO tangles; and report it here as a picture>
```
<img src="img/design/lev_structure_map.png">

# Structural over complexity chart
```
<Applying Structure 101 to your project, version to be delivered on june 4, produce the structural over complexity chart; and report it here as a picture>
```
<img src="img/design/structure_comp.png">



# Size metrics

```
<Report here the metrics about the size of your project, collected using Structure 101>
```



| Metric                                    | Measure |
| ----------------------------------------- | ------- |
| Packages                                  |     5    |
| Classes (outer)                           |      48   |
| Classes (all)                             |       48  |
| NI (number of bytecode instructions)      |      9882   |
| LOC (non comment non blank lines of code) |     4249    |



# Items with XS

```
<Report here information about code tangles and fat packages>
```

| Item | Tangled | Fat  | Size | XS   |
| ---- | ------- | ---- | ---- | ---- |
| ezshop.it.polito.ezshop.data.EZShop|	 	| 147	|3,704|	680|



# Package level tangles

```
<Report screen captures of the package-level tangles by opening the items in the "composition perspective" 
(double click on the tangle from the Views->Complexity page)>
```
<img src="img/design/complexity.png">

# Summary analysis
```
<Discuss here main differences of the current structure of your project vs the design delivered on April 30>
<Discuss if the current structure shows weaknesses that should be fixed>
```
## Main differences
- Order does not extend BalanceOperation
- Added TicketEntryClass with dependencies on SaleTransaction and ProductType
- Added map AttachedCards in EZShop
- Added class Connect (db interaction)

## Weaknesses and possible improvements
 No weakness since the only fat class is EZShop and that is due to the fact that this is a facade class. 
