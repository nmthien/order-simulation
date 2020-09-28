<h1>Orders Simulation Challenge</h1>

<h2>Run the code</h2>
Opening the project with IntelliJ and running it from the IDE would be the best way to test out the code. You can set two arguments for the main method:
<ul>
    <li><code>-i</code>: input file path (required)</li>
    <li><code>-r</code>: ingestion rate (optional)</li>
</ul>
It's useful for testing to change the pick-up time range (2s - 6s by default). Those values can be changed by modifying the constants defined in Constants class.
Since they're not required to be configurable by the challenge description, I didn't add main method's arguments for them.

<h2>Overflow shelf handling</h2>
<ul>
    <li>When an order comes in and there's no room on its corresponding single temperature shelf, it'll be placed on overflow shelf.</li>
    <li>When overflow shelf is full and there's a need to place an order on it, the simulator will make room on overflow shelf by:
        <ul>
            <li>Check if there's an order that could be moved from overflow shelf to a single temperature shelf and move it.</li>
            <li>If there're multiple such orders, it will select the one with lowest inherent value to prevent it from going bad before getting picked up.</li>
            <li>If there's no such order, it will select a random order to remove (as required by the challenge description).</li>
        </ul>
    </li>
    <li>We could have orders moved from overflow shelf to single temperature shelves whenever possible to extend the orders' lives, since single temperature shelves have lower <code>shelfDecayModifier</code> value. But that is an optimization out of the challenge's scope.</li>
</ul>

<h2>Design considerations</h2>


A full life-cycle of an order would be:
<ol>
    <li>Order received from client</li>
    <li>Order prepared by kitchen</li>
    <li>Order ready</li>
    <li>Order placed on shelf to wait for pick up</li>
    <li>Dispatcher dispatches a courier to pick up order with an ETA</li>
    <li>Order picked up</li>
    <li>Order's delivery status tracked</li>
</ol>
The simulation focuses on step 3-4-5-6 of this life-cycle.
<h3>Dispatcher</h3> 
In real life architecture, the dispatcher should would as a stand-alone service listening on an <code>Order</code> topic for incoming orders and dispatch couriers accordingly.
When an order is picked up, its picked-up time would be updated by calling <code>order.sePickedUpTime(currentTime)</code>.
<p>However for simplicity and the purpose of the simulator to have each order picked up randomly from 2-6s, the dispatcher is simplified to be a function call to assign the pick up time of an order upon its arrival on a shelf.</p> 

<h3>Shelf</h3>
Shelf could be abstracted and have different shelf types extended it.
However since the simulation is dealing with only two shelf types (single temperature and overflow) and most of the complicated logics are for overflow shelf, I decided to not overdo it and use a single class with ShelfType enum to distinguish between different shelf types.
 