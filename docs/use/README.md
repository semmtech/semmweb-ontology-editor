# SEMMweb Ontology Editor


## Installation
- Install Java SDK (JDK) version 8; make sure this is a 32-bit version (x86) as other versions DO NOT WORK.
- Install the SEMMweb Ontology Editor to a location on your Windows computer and run it.


## User interface

The SEMMWeb Editor displays the information stored in a semantic model, often referred to as an ontology, and provides ways to manipulate that information. The user interface has been created with just that in mind. The picture below shows a SEMMWeb Editor in which the semantic model 'Top level permit ontology' (from [DigiChecks](https://semmtech.github.io/digichecks-ontology/) has been opened. As can be seen, the interface contains a number of windows, called `views`. Which views are displayed exactly depends on the chosen `perspective`. Any available perspective can be customized to suit your own taste. The perspective opened by default in the SEMMweb Editor is the Ontology perspective. This perspective is geared specifically towards viewing and modifying semantic models.

![semmweb-modelopened](img/semmweb-modelopen-digichecks-resourceopen-verification.png?raw=true "The SEMMweb Editor with a semantic model opened")


### Perspectives

The windows, or `views`, shown by the SEMMweb Editor are laid out according to the `perspective` that is enabled. In other words, a perspective states which views are supposed to be open and where these views should be located on screen. Therefore, one perspective may suit your needs better than another depending on what your goals are. 

#### Switching perspectives
Switching to another perspective can be done in two ways. Among the main menus, the "Window" menu contains the option called "Open Perspective". Using this option you can select another perspective to which you would like to switch.

![semmweb-window-openperspective](img/semmweb-window-openperspective.png?raw=true "Switching perspectives via the main menu")

Alternatively, you can use the main toolbar. This bar provides quick access to the perspectives you have opened recently. This functionality is located on the right of the toolbar. By simply clicking one of these perspectives, the SEMMweb Editor will switch to that perspective.

![semmweb-maintoolbar-openperspective](img/semmweb-maintoolbar-openperspective.png?raw=true "Switching perspectives via the main toolbar")

#### Customising a perspective
Although a perspective contains a default setting for which views should be open and where they should be shown, these settings can be customised to your needs. You can open a new view in the "Show view" option of the "Window" menu An opened perspective will remember how you adjusted the views in the user interface. As such, by simply opening, closing, moving, and resizing windows, you are automatically customising the perspective you are working in. If you consider your customised perspective to be one that should have a different name, the "Window" menu provides an option to do just that - see the "Save Perspective As..." option in the first picture on this page. If you instead would like the default settings of the perspective back, simply select the "Reset Perspective..." option in that very same "Window" menu.


### SEMMweb Model editor (centre window)

The centre window acts as an editor for files that you open via the Project Navigator view. In case that file is a .txt file, this centre window will be filled with a text editor. In case of a semantic model (a.k.a. ontology), the editor opened will be one for editing models: the SEMMweb Model editor. The first thing that this editor shows is an overview of the model contents (the ontology resource, classes, properties, and so on), along with the means of easily creating new resources of these kinds. 

Opening a specific resource within the model in this central editor is typically done by double clicking that resource with the mouse cursor in any of the views or indeed in the central editor. You can switch between opened resources from the model by means of the grey sidebar on the left of this central window. The SEMMweb Model editor will show all statements with that opened resource as the subject and allows you to remove or add statements (e.g., through simple drag and drop actions of properties and resources shown in other views). For an opened resource, the editor provides a number of tabs. Next to the one showing statements (i.e., the 'Properties' tab), a 'Diagram' tab visualizes the statements as a network of nodes and edges. Further tabs may be available to edit content. The 'Instances' and 'Instance Behaviour' tabs, available for classes that have been opened, show the instances belonging to that class and the restrictions acting on that class (similar to the separate Instances view and Instance Behaviour view).


### Views in the Ontology perspective

The Ontology perspective contains a number of views alongside the Semantic Model editor in the centre. 
Two of these views allow you to organize your semantic models -- offline and online:

- **Project Navigator**  
  Shows your projects. Each project consists of a folder structure containing Models, SPARQL queries, images, or other files associated with a model. If you start working with the SEMMweb Editor, you will want to create a project here and add your first semantic model (i.e., an "RDF/OWL Ontology file").
- **Laces LDP**  
  Provides access to the online Linked Data Platform of the Laces suite. Using this view you can log in to the platform with your account, download published models, or publish your own semantic model on the internet. This platform offers a number of benefits, including the means for software applications to run SPARQL queries to select and retrieve knowledge from a model using the Web.

The remaining views included in this perspective show content from the semantic model that is currently open before you in the centre window (a.k.a. the SEMMweb Model editor).
- **Classes**  
  Shows the hierarchy of classes from the model, drawing on the RDFS Class definition.
- **Classes of Individuals**  
  Shows a hierarchy of classes from the model, drawing on the OWL Class definition (which excludes a number of more generic RDFS classes).
- **Properties**  
  Shows the hierarchy of properties from the model.
- **Instances**  
  Shows instances belonging to the class that you have dragged and dropped onto the view.
- **Instance Behaviour**  
  Shows any restrictions applicable to the class that you have dragged and dropped onto the view, drawing on the OWL Restriction definition.
- **Network Navigator**  
  Shows statements on the resource that you have dragged and dropped onto the view.
- **Triples**  
  Shows all triples in the model.


## Disclaimer

The software application within this project is offered as-is, and has not been developed actively since 2016.
