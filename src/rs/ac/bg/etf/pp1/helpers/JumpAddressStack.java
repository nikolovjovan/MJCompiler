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
            jumpAddressListStack.push(currentJumpAdressList);
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

    public List<Integer> getJumpAddressList(boolean trueJump) {
        if (currentJumpAdressList == null) {
            return null;
        }
        return trueJump ? currentJumpAdressList.trueJumpAddressList : currentJumpAdressList.falseJumpAddressList;
    }

    public void insertJumpAddress(boolean trueJump, int address) {
        if (currentJumpAdressList == null) return;
        if (trueJump) {
            currentJumpAdressList.trueJumpAddressList.add(address);
        } else {
            currentJumpAdressList.falseJumpAddressList.add(address);
        }
    }
}