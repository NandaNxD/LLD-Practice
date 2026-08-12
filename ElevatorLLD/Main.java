package ElevatorLLD;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.UUID;

/**
 * Requirements:
 * 
 * Elevator that takes in Hall request and CabinRequest
 * 
 * Hall request is UP or DOWN, where user would like to go next
 * CabinRequest is mainly floor number, which is pressed from inside cabin
 * 
 * Entity:
 * 
 * Elevator
 * 
 * ElevatorController
 * 
 * Direction
 * 
 * Request
 *  - HallRequest
 *  - CabinRequest
 * 
 * 
 * 
 */

enum Direction{
    UP,
    DOWN,
    IDLE
}

class Request{
    private int floorNumber;

    public Request(int floorNumber){
        this.floorNumber=floorNumber;
    }

    public int getFloorNumber() {
        return floorNumber;
    };
}

class CabinRequest extends Request{
    public CabinRequest(int floorNumber){
        super(floorNumber);
    }
}

class HallRequest extends Request{
    private Direction direction;

    public HallRequest(int floorNumber, Direction direction){
        super(floorNumber);
        this.direction=direction;
    }

    public Direction getDirection() {
        return direction;
    }
}


class Elevator{ 

    String id;

    int minFloor;
    int maxFloor;

    int currentFloor;

    Direction direction=Direction.IDLE;

    List<Request> requests=new ArrayList<>();

    public Elevator(String id, int minFloor, int maxFloor,int currentFloor){
        this.id=id;
        this.minFloor=minFloor;
        this.maxFloor=maxFloor;
        this.currentFloor=currentFloor;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean addRequest(Request request){
        int floorNumber=request.getFloorNumber();

        if(floorNumber<minFloor || floorNumber>maxFloor){
            System.out.println("Invalid floor number");
            return false;
        }
    
        requests.add(request);
        return true;
    }

    public void stop(){

    }

    private List<Request> getAllRequestsForFloorNumber(List<Request> requests, int floorNumber){
        List<Request> resultList=new ArrayList<>();

        for(Request request: requests){
            if(request.getFloorNumber()==floorNumber){
                resultList.add(request);
            }
        }

        return resultList;
    }

    private Request getClosestRequestToFloor(List<Request> requests, int floorNumber, Direction direction){
        int distance=Integer.MAX_VALUE;
        Request closestFloorRequest=null;

        for(Request request:requests){
            int distanceToFloor=Math.abs(request.getFloorNumber()-floorNumber);

            if(direction==null){
                if (distanceToFloor < distance) {
                    distance = distanceToFloor;
                    closestFloorRequest = request;
                }
            }
            else{
                if((request.getFloorNumber()>floorNumber && direction==Direction.UP) 
                    || (request.getFloorNumber()<floorNumber && direction==Direction.DOWN)){
                    if (distanceToFloor < distance) {
                        distance = distanceToFloor;
                        closestFloorRequest = request;
                    }
                }
            }
           

        }

        return closestFloorRequest;
    }



    public void tick(){
        System.out.println("Elevator: "+id+", CurrentFloor: "+currentFloor+ " ,Direction: "+direction);
        /**
         * If requests is empty, make direction to be idle and hold
         */

        if(requests.isEmpty()){
            direction=Direction.IDLE;
            return;
        }

        /**
         * if current floor is in requests, remove it, call stop
         */
        List<Request> currentFloorRequests= getAllRequestsForFloorNumber(requests,currentFloor);
        if(!currentFloorRequests.isEmpty()){
            requests.removeAll(currentFloorRequests);
            stop();
            return;
        }

        if(direction==Direction.IDLE){
            // get closest request from the current floor, and move in that direction
            Request request=getClosestRequestToFloor(requests,getCurrentFloor(), null);
            if(request.getFloorNumber()<currentFloor){
                direction=Direction.DOWN;
            }
            else{
                direction=Direction.UP;
            }
        }
        else{
            // get all the requests below the current floor, if no requests are there, then reverse the direction.
            Request request= getClosestRequestToFloor(requests, getCurrentFloor(), direction);
            if(request==null){
                direction=direction==Direction.UP?Direction.DOWN:Direction.UP;
            }
            else{
                if(direction==Direction.UP){
                    currentFloor++;
                }
                else{
                    currentFloor--;
                };
            }
        }

    }

}

interface ElevatorFindingStrategy{
    Elevator findBestElevator(List<Elevator> elevators, int floorNumber, Direction direction);
}

class ClosestElevatorFindingStrategy implements ElevatorFindingStrategy{

    @Override
    public Elevator findBestElevator(List<Elevator> elevators, int floorNumber, Direction direction) {
        Elevator closestElevatorMovingInCurrentDirection=findClosestElevatorMovingInCurrentDirection(elevators,floorNumber,direction);

        if(closestElevatorMovingInCurrentDirection!=null){
            return closestElevatorMovingInCurrentDirection;
        }

        Elevator closestIdleElevator=findClosestIdleElevator(elevators,floorNumber);

        if(closestIdleElevator!=null){
            return closestIdleElevator;
        }

        Elevator closestElevator=findClosestElevator(elevators,floorNumber);

        return closestElevator;
    }

    private Elevator findClosestElevatorMovingInCurrentDirection(List<Elevator> elevators, int floorNumber, Direction direction){
        int distance=Integer.MAX_VALUE;
        Elevator closestElevator=null;

        for(Elevator elevator:elevators){
            if(elevator.direction==direction){

                int elevatorDistanceToFloor=Math.abs(floorNumber-elevator.getCurrentFloor());

                if((direction==Direction.UP &&  elevator.getCurrentFloor()<floorNumber) 
                        || (direction == Direction.DOWN && elevator.getCurrentFloor() > floorNumber)){
                    if(distance>elevatorDistanceToFloor){
                        distance = elevatorDistanceToFloor;
                        closestElevator = elevator;
                    }
                }
            }
        }

        return closestElevator;
    }

    private Elevator findClosestIdleElevator(List<Elevator> elevators, int floorNumber){
        int distance = Integer.MAX_VALUE;
        Elevator closestElevator = null;

        for (Elevator elevator : elevators) {
            if (elevator.direction == Direction.IDLE) {

                int elevatorDistanceToFloor = Math.abs(floorNumber - elevator.getCurrentFloor());

                if (distance > elevatorDistanceToFloor) {
                    distance = elevatorDistanceToFloor;
                    closestElevator = elevator;
                }
            }
        }

        return closestElevator;
    }

    private Elevator findClosestElevator(List<Elevator> elevators, int floorNumber) {
        int distance = Integer.MAX_VALUE;
        Elevator closestElevator = null;

        for (Elevator elevator : elevators) {
            int elevatorDistanceToFloor = Math.abs(floorNumber - elevator.getCurrentFloor());

            if (distance > elevatorDistanceToFloor) {
                distance = elevatorDistanceToFloor;
                closestElevator = elevator;
            }
        }

        return closestElevator;
    }

}

class ElevatorController{
    private List<Elevator> elevators;
    private ElevatorFindingStrategy elevatorFindingStrategy;

    public ElevatorController(List<Elevator> elevators, ElevatorFindingStrategy elevatorFindingStrategy){
        this.elevators=elevators;
        this.elevatorFindingStrategy=elevatorFindingStrategy;
    }

    public boolean addHallRequest(int floorNumber, Direction direction){
        Elevator elevator=elevatorFindingStrategy.findBestElevator(elevators,floorNumber, direction);
        System.out.println(elevator.id);
        return elevator.addRequest(new HallRequest(floorNumber, direction));
    }

    public void tick(){
        for(Elevator elevator:elevators){
            elevator.tick();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Elevator elevator1=new Elevator("1", 0, 9, 0);
        Elevator elevator2 = new Elevator("2", 0, 9, 1);

        ElevatorController elevatorController=new ElevatorController(List.of(elevator1,elevator2), new ClosestElevatorFindingStrategy());

        boolean requestAdded=elevatorController.addHallRequest(2, Direction.UP);
        elevator1.addRequest(new Request(3));

        for(int i=0;i<8;i++){
            elevatorController.tick();
            System.out.println();
        }

        
    }
}

// javac ElevatorLLD/Main.java && java ElevatorLLD/Main