package wheresmymoney.command;

import wheresmymoney.category.CategoryFacade;
import wheresmymoney.ExpenseList;
import wheresmymoney.Parser;
import wheresmymoney.RecurringExpenseList;
import wheresmymoney.DateUtils;
import wheresmymoney.exception.InvalidInputException;
import wheresmymoney.exception.WheresMyMoneyException;

import java.util.HashMap;
import java.time.LocalDate;

public class AddCommand extends Command {

    public AddCommand(HashMap<String, String> argumentsMap) {
        super(argumentsMap);
    }

    /**
     * Add a expense or recurring expense after prasing through arguments
     * @param expenseList List of normal expenses
     * @param recurringExpenseList List of recurring expenses
     */
    @Override
    public void execute(ExpenseList expenseList, CategoryFacade categoryFacade, RecurringExpenseList recurringExpenseList) 
            throws WheresMyMoneyException {
        try {
            float price = Float.parseFloat(argumentsMap.get(Parser.ARGUMENT_PRICE));
            String description = argumentsMap.get(Parser.ARGUMENT_DESCRIPTION);
            String category = argumentsMap.get(Parser.ARGUMENT_CATEGORY);
            boolean isContainDateKey = argumentsMap.containsKey(Parser.ARGUMENT_DATE);
            if (isContainDateKey && !this.isRecur()) {
                LocalDate dateAdded = DateUtils.stringToDate(argumentsMap.get(Parser.ARGUMENT_DATE));
                expenseList.addExpense(price, description, category, dateAdded);
                categoryFacade.addCategory(category, price);
            } else if (!isContainDateKey && !this.isRecur()) {
                expenseList.addExpense(price, description, category);
                categoryFacade.addCategory(category, price);
            } else if (isContainDateKey && this.isRecur()) {
                LocalDate lastAddedDate = DateUtils.stringToDate(argumentsMap.get(Parser.ARGUMENT_DATE));
                String frequency = argumentsMap.get(Parser.ARGUMENT_FREQUENCY);
                recurringExpenseList.addRecurringExpense(price, description, category, lastAddedDate, frequency);
            } else {
                String frequency = argumentsMap.get(Parser.ARGUMENT_FREQUENCY);
                recurringExpenseList.addRecurringExpense(price, description, category, frequency);
            }
        } catch (NullPointerException | NumberFormatException e) {
            throw new InvalidInputException("Invalid Arguments");
        }
    }
    
}
