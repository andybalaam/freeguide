This branch contains a modified version of FreeGuide using the MVC
technique, among others, in the programmesPanel.


What is the MVC paradigm?

In the MVC paradigm the operation of an application, or part of it, is
modularized into Model, View and Controller objects. Model reflects the
data the application (or part of it) works on. View object is used to
show visually the data represented by the model. Controller is an UI
object used to modify and edit the data through the Model. In many cases
the View and Controller are combined into a single UI object that shows
the status of a Model and allow the user to edit its state.

Controller responds to user input (mouse and keyboard events) and calls
appropriate methods in the Model or View to make the desired change. The
View renders the state of the Model in response to paint requests or
notification events from the Model. The Model manages its data by
responding to queries, modifying its data in response to modify requests
and sends data modification notifications to its Views.

Typically the View/Controller UI component is implemented as a class.
The Model is defined as an interface. To send notifications to the UI
components, the Model defines one or more Event classes that describe
what has happened to a model. The Model also defines
add/removeEventListener() methods, which the View/Controllers can (and
should) call to register for receiving notifications. When an event
happens the Model calls all registered event listeners. Each event
listener is an object that implements the EventListener intreface that
the Model defines.
 

Why use the MVC paradigm?

First of all, MVC allows you to modularize and separate your
application's logic from its user interface. The modularization itself
helps structuring the source code. In addition, it is easier to make
modifications to your UI code, to try out new ideas, when it is not so
tightly coupled with the logic of the application. Ultimately, you could
create alternative UIs, even one graphical and one textual, sharing all
the main logic of the application.

MVC allows to easily present the same data in a number of different
different ways simultaneously and keeping them in synch. E.g. show the
contents of a file in two editor windows at the same time. Or show TV
programme information simultaneously in a time graph and textual list.
This is done by hooking two (different or identical) View objects to a
single Model object.

MVC lessens the need to copy data from a format to another, thus
simplifying and speeding an application. Consider a database client that
wants to show the contents of a 10000 record table in a table component.

Without MVC approach, the table UI component expects to get an array of
the data it should be showing. So the client must copy all the 10000
records from the database into an array, create a table component and
pass it the array.

Using MVC, you make a Model that knows how to fetch a particular row
from the database, when asked. You then create the table View and pass
to it your Model object. When the table is painted, it requests the
needed data from the model, usually something like 20 records from the
database.

MVC also makes it easier to create new views for general use. If you are
making a table UI component without the MVC paradigm, you have to think
carefully, in what format you should store the data into the table
component so that the component can show it easily and at the same time
have it in a format that is easy for the users of the component. You may
end up providing a number of alternatives, like array, Vector, Map etc.
and then code a number of table drawing routines for each datatype. And
still many of the component's users would not be happy, because it
doesn't support the format they would like. With MVC, you just declare
what are the operations you need to be able to draw a table (basically
getElementAt(x,y)) in a Model interface and then implement your table
View based on that. Each user can fit their data as they like to the
Model interface.

This is actually how the JTable class as well as many other classes in
Swing work. javax.swing.JTable implements the View/Controller part and
javax.swing.table.TableModel defines the Model API. The users of the
JTable class need to implement the TableModel interface.

Notice that the Model is usually quite tightly tied to the particular
representation the View/Controller is using (be it a table or a graph or
something else), whereas the application provides operations that work
on a different abstraction level (e.g. get today's favourite programs).
Implementing the Model interface requires thus writing an adapter class
that maps the concepts of the application to the concepts of the
View/Controller.

Notice also that in some cases you can and even should share a single
Model interface with multiple View/Controllers. E.g. a table and graph
object might share a common Model interface. This way the application
can use a single Model implementation and the UI designer is free to
choose whatever combination of UI components to show the application's
state. In Swing the ButtonModel is shared between check boxes, radio
buttons and normal buttons.


How this all is done in the FreeGuide MVC branch?

There is a general purpose View/Controller component for presenting rows
of arbitrary strips called StripView. StripView operates on Strip
objects that contain the start and end of the strip and its value, an
arbitrary Object that the view doesn't directly access in any way.
StripView's purpose is to lay out the Strips by mapping the start and
end values from the users (arbitrary) co-ordinate system to the pixel
coordinates of StripView.

To get hold to the various Strip objects, the StripView uses a model
called StripView.Model. This is an interface that defines the methods
that StripView needs to do its job.

To use the general StripView View/Controller in our FreeGuide
application, we need to define a Model compatible to StripView.Model
that knows how to fetch programme information when StripView asks for
it. ProgrammeStripModel is this specific class that implements the
StripView.Model interface. The implementation of this model is far from
perfect: it just stores the programme data it gets elsewhere into an
ArrayList of TreeMaps. To fully take advantage of the MVC model and
avoid needless copying, it should directly ask the XMLTV backend for the
available data and act just as an adapter between what StripView needs
and what the backend provides. However, since that backend currently
just stores the data into a bunch of Vectors, it would have needed to be
modified considerably. Therefore the current naive implementation.


What about all these other new classes and changes to the existing ones?

The StripView implementation defines some interfaces and classes that
have not been mentioned yet. They are not directly related to the MVC
paradigm, more to the particular implementation that StripView uses.
Also a number of classes needed to be modified quite heavily to fit into
the new implementation.

To avoid allocating a number of JLabel objects inside the StripView, it
uses a rubber-stamp technique to paint its content. Whenever StripView
needs to paint, it iterates through all the Strips using a single
renderer object that it sets to a specific location, sets its content to
match the Strip's data and commands the renderer to paint itself. Then
it repeats the process with the next Strip. The StripRenderer interface
defines the API that StripView uses to ask for a suitably configured
Component object for each Strip.

This method is what makes the StripView much faster than the previous
implementation. This is also the method that JTable uses. This is also
the part of the implementation that is quite tricky, especially with
input events, since there are actually no real Components inside the
StripView, just after-images of a single renderer Component running
through the StripView canvas.

ProgrammeJLabel is the prime candidate to be a StripRenderer, since it
knows how to paint Programme information. However, as it was, it was not
suitable for the task. It was tied really tightly to the other
components of the program and also contained logic that should have been
elsewhere. E.g. its constructor took a ViewerFrame as one of its
parameters and it used this parameter to get to some of the information
it needed. But this meant that the object could be only used inside a
ViewerFrame! And this was completely unnecessary, since to be able to
show programme info, you only need a Programme object and the
information, whether it is a favourite or not. It also took parameters
halfHorGap, widthMultiplier, halfVerGap and channelHeight. These are
again things that a ProgrammeJLabel should not be concerned and that
prevent it to be used in any other container that it was originally
used. To make ProgrammeJLabel to function as its own independent
component, lots of code was needed to be moved to classes it really
belonged.

The View part of StripView was now able to use ProgrammeJLabel as a
StripRenderer. However, the Controller functionality of the StripView
needed another component to use as a Strip editor that would process
user input from keyboard and mouse and convert those to change
operations to the underlying model. However, there are two problems.
First of all, the data shown by a ProgrammeJLabel comes from a
combination of Programme and from the selection and favourite
collections. Secondly, I hadn't implemented setValueAt in the
StripView.Model (because I wasn't happy with any general API that I came
up), so I had the problem how to pass the edit information from the
Strip editor through the StripView that knew nothing about Programmes
and favourites to the underlying application. I ended up converting
ProgrammeJLabel to use the MVC paradigm by making it have a model
object, too. This is not the most proper way to do things. Furthermore,
while I have separate rederer and editor objects, they are both of the
same ProgrammeRenderer type that is really an editor and a renderer. It
would be better to have separate classes for these.

ViewerFrame contains many new inner classes. Most of them don't really
need to be inner classes, it's just easier to prototype when you don't
have to create so many new files.

BorderChanger is used highlight the containing component when one of the
Strips inside StripView has keyboard focus. FocusJScrollPane is a
JScrollPane that uses BorderChanger to to highlight its border, when its
view gets focus. BorderChanger is also used to highlight the border of
splitPaneChanProg and splitPaneMainDet when they have focus.

ViewerFrame.ProgrammeRenderer is the class used for StripView's renderer
and editor objects, as mentioned above. This class should be in a
separate file. Much of the code was lifted out of ProgrammeJLabel as it
was inappropriate there. Much of the code shouldn't be here either, but
rather in the business logic layer of the application. The rest needs
some polishing.

[there are also some other changes in ViewerFrame]


How would I go about making a new view?

Usually when you make a new UI component, you make a View/Controller.
Some examples are JCheckBox, JComboBox and JTextField that let you view
the state of their Model and also control it with editing actions. Most
of the time when you make a new View/Controller, you make a new Model
interface for it. In many cases the Model comes naturally when you
implement the View/Controller and in your implementation find out what
are the particular queries you need to make from the Model to be able to
paint or edit the Model's state.


How would I alter the model(s) without breaking the MVC logic?

First of all, you should of course not modify the interface. Second,
your model should not call any methods of View/Controllers. If you want
to change the content of a View, you should just generate some kind of
Event object that you send to all the registered parties. It's the
View's job to register itself to listen to your model. Similarly, your
model might listen for events in other models. E.g. you might implement
a StripView.Model on top of a preferences model that stores favourites
information and a programme collection that stores information about
available programmes. You would answer queries made to you by querying
the favourites models or programme collection as appropriate. You would
also listen to those models' events and in response to them send your
own change notifications to the objects that are listening you.


What is the algorithm followed when you enter a new day?

In a pure MVC application, nothing special needs to be done. Entering a
new day is done by scrollling the StripView to show the new time range.
This triggers a paint request to the StripView. Its paint method then
calculates what time ranges the exposed pixel coordinates refer to and
queries the ProgrammeStripModel to get the programme information. The
ProgrammeStripModel would then query the XMLTV backend which would
either read and parse new information or pass it from its cache.
However, since the XMLTV backend gives it data in day to day format that
must be converted to a more suitable format, a new ProgrammeStripModel
is created for each day. This happens at the same place where the actual
drawing of the ProgrammeJLabels took place in the original code, around
line 1438 of file ViewerFrame.java in function drawProgrammes().


Risto Kankkunen, Oct 2004
