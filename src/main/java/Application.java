import component.Receiver;
import component.Sender;
import helper.FileOperations;

public class Application {

    public static void main(String[] args) {
        try{
            // Program args: receiver
            if(args[0].equals("receiver")){
                new Receiver();
            }
            // Program args: file/path/ sender
            else if(args[1].equals("sender")){
                new Sender(FileOperations.readFileAndReturnBytePartsAsPackets(args[0]));
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

}
