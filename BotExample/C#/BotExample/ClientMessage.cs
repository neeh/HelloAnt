using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Web.Script.Serialization;
using System.Collections;

namespace BotExample
{
    // Standard message to be sent to the server
    class ClientMessage
    {
        public string type;
        public object content;

        public string Serialize()
        {
            return (new JavaScriptSerializer()).Serialize(this);
        }
    }

    /* Login message */
    class LoginMessage : ClientMessage
    {
        public LoginMessage(string token, bool training)
        {
            type = "login";
            content = new LoginMessageContent() { token = token, mode = training ? "training" : "regular" };
        }
    }
    class LoginMessageContent
    {
        public string token;
        public string mode;
    }

    /* Game actions message */
    class GameActionsMessage : ClientMessage
    {
        public GameActionsMessage()
        {
            type = "gameactions";
            content = new GameActionsMessageContent();
        }
        public void AddMove(int col, int row, string dir)
        {
            ((GameActionsMessageContent)content).moves.Add(new ArrayList() { row, col, dir });
        }
    }
    class GameActionsMessageContent
    {
        public List<ArrayList> moves;
        public GameActionsMessageContent()
        {
            moves = new List<ArrayList>();
        }
    }
}
