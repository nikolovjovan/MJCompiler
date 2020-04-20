package rs.ac.bg.etf.pp1.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class JumpAddressStack {

    private static class JumpAddressList {
        ArrayList<Integer> falseJumpAddressList, trueJumpAddressList;

        public JumpAddressList() {
            falseJumpAddressList = new ArrayList<>();
            trueJumpAddressList = new ArrayList<>();
        }
    }

    private Stack<JumpAddressList> jumpAddressListStack;
    private JumpAddressList currentJumpAdressList;

    public JumpAddressStack() {
        jumpAddressListStack = new Stack<>();
    }

    public void createJumpAddressList() {
        if (currentJumpAdressList != null) {
            jumpAddressListStack.add(currentJumpAdressList);
        }
        currentJumpAdressList = new JumpAddressList();
    }

    public void popJumpAddressList() {
        if (!jumpAddressListStack.isEmpty()) {
            currentJumpAdressList = jumpAddressListStack.pop();
        } else {
            currentJumpAdressList = null;
        }
    }

    public List<Integer> getTrueJumpAddressList() {
        if (currentJumpAdressList == null) {
            return null;
        }
        return currentJumpAdressList.trueJumpAddressList;
    }

    public List<Integer> getFalseJumpAddressList() {
        if (currentJumpAdressList == null) {
            return null;
        }
        return currentJumpAdressList.falseJumpAddressList;
    }

    public boolean insertJumpAddress(boolean trueJump, int address) {
        if (currentJumpAdressList == null) {
            return false;
        }
        if (trueJump) {
            currentJumpAdressList.trueJumpAddressList.add(address);
        } else {
            currentJumpAdressList.falseJumpAddressList.add(address);
        }
        return true;
    }

    public boolean insertTrueJumpAddress(int address) {
        return insertJumpAddress(true, address);
    }

    public boolean insertFalseJumpAddress(int address) {
        return insertJumpAddress(false, address);
    }
}