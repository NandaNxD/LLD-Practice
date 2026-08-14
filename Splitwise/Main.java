package Splitwise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

class User{
    private final String id;
    private final String name;

    public User(String name){
        this.id=UUID.randomUUID().toString();
        this.name=name;
    }

    public String getId() {
        return id;
    }
}

class UserGroup{
    private final String id;
    private final String name;

    Set<User> userList;

    public UserGroup(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.userList=ConcurrentHashMap.newKeySet();
    }

    public UserGroup(String name, Set<User> userList){
        this.id=UUID.randomUUID().toString();
        this.name=name;
        this.userList=userList;
    }

    void addUser(User user){
        userList.add(user);
    }
}



class UserGroupService{
    Map<String, UserGroup> userGroupMap=new HashMap<>();

    void createUserGroup(String userGroupName){
        UserGroup userGroup=userGroupMap.get(userGroupName);

        if(userGroup==null){
            userGroupMap.put(userGroupName, new UserGroup(userGroupName));
        }
    }

    void createUserGroup(String userGroupName,Set<User> users) {
        UserGroup userGroup = userGroupMap.get(userGroupName);

        if (userGroup == null) {
            userGroupMap.put(userGroupName, new UserGroup(userGroupName,users));
        }
    }

    void addUserToGroup(String groupName, User user) throws Exception{
        UserGroup userGroup=userGroupMap.get(groupName);

        if(userGroup==null){
            throw new Exception("User group not found");
        }

        userGroup.addUser(user);
    }
}

abstract class Split{
    private User user;
    private double amount;

    public Split(User user){
        this.user=user;
    }

    public Split(User user,double amount) {
        this.user = user;
        this.amount=amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public User getUser() {
        return user;
    }

    public double getAmount() {
        return amount;
    }
}

class ExactSplit extends Split{
    public ExactSplit(User user,double amount){
        super(user, amount);
    }
}

class EqualSplit extends Split {
    public EqualSplit(User user) {
       super(user);
    }
}

class PercentageSplit extends Split{
    double percentage;

    public PercentageSplit(User user,double percentage){
        super(user);
        this.percentage=percentage;
    }

    public double getPercentage() {
        return percentage;
    }

}


class Expense{
    String id;
    String description;

    User paidBy;
    double amount;

    List<Split> splits;

    public Expense(String description, User paidBy, double amount,List<Split> splits){
        id=UUID.randomUUID().toString();
        this.description=description;
        this.paidBy=paidBy;
        this.amount=amount;
        this.splits=splits;
    }
}

enum ExpenseType{
    EXACT_SPLIT,
    EQUAL,
    PERCENTAGE
}

class ExpenseFactory{

    public static Expense createExpense(ExpenseType expenseType,String description, User paidBy, double amount, List<Split> splits) throws Exception{
        if(expenseType==ExpenseType.EQUAL){
            splits.stream().forEach((split)->{
                split.setAmount(amount/(splits.size()*1.0));
            });
            return new Expense(description,paidBy,amount,splits);
        }
        else if(expenseType==ExpenseType.EXACT_SPLIT){
            return new Expense(description, paidBy, amount, splits);
        }
        else if(expenseType==ExpenseType.PERCENTAGE){

            splits.stream().forEach((split) -> {
                PercentageSplit percentageSplit=(PercentageSplit) split;
                split.setAmount((percentageSplit.getPercentage()/100.0)*amount);
            });

            return new Expense(description, paidBy, amount, splits);
        }
        else{
            throw new Exception("Expense type not implemented");
        }
    }


}

class SplitwiseService{
    Map<String, Map<String, Double>> balanceSheet=new HashMap<>();

    Map<String, List<Expense>> groupExpenseMap=new HashMap<>();

    public void addExpense(String groupId,Expense expense){

        groupExpenseMap.compute(groupId,(k,v)->{
            if(v==null){
                return new ArrayList<>(List.of(expense));
            }
            v.add(expense);
            return v;
        });

    }

    public void calculateBalanceSheet(String groupId){
        // what do i want to do
        /**
         * Get everyones price,
         * put the user who paid as +ve with the total amount
         * put the users who owe, as negative
         */

        List<Expense> expenses=groupExpenseMap.get(groupId);

        Map<User,Double> userBalances=new HashMap<>();

        for(Expense expense:expenses){
            User user=expense.paidBy;
            double amount=expense.amount;

            expense.splits.forEach((split)->{
                userBalances.compute(split.getUser(), (k,v)->{
                    double oldValue=v==null?0:v;
                    System.out.println(split.getAmount());
                    if(split.getUser().equals(user)){
                        return oldValue+amount-split.getAmount();
                    }
                    else{
                        return oldValue-split.getAmount();
                    }
                });
            });
        }

        System.out.println(userBalances);

    }
}

public class Main {
    public static void main(String[] args) throws Exception{

        User nandan=new User("Nandan");
        User naveen=new User("Naveen");

        double totalAmount=15000;

        List<Split> splits=new ArrayList<>();
        splits.add(new EqualSplit(naveen));
        splits.add(new EqualSplit(nandan));

        Expense expense=ExpenseFactory.createExpense(ExpenseType.EQUAL, "GOA Trip", nandan, totalAmount, splits);

        SplitwiseService service=new SplitwiseService();

        service.addExpense("first", expense);

        service.calculateBalanceSheet("first");

    }
}
// javac Splitwise/Main.java && java Splitwise/Main 