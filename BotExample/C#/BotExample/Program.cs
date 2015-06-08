using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.Web.Script.Serialization;
using Microsoft.CSharp.RuntimeBinder;
using System.IO;

namespace BotExample
{
    class Program
    {
        /* CHANGE SETTINGS HERE */
        private const string DEFAULT_TOKEN = "abc";
        private const bool DEFAULT_TRAINING = true;
        // Directory.GetCurrentDirectory() while running in debug-mode = [PROJECT-PATH]/bin/debug
        private readonly string SAVE_DIRECTORY = Directory.GetCurrentDirectory();

        private Socket sock;
        // Object used to convert JSON to a C# object
        private JavaScriptSerializer serializer;

        private bool inGame;
        private bool muted;
        private GameMap map;
        private string token;

        public Program(string ip, string token, bool training)
        {
            this.token = token;
            inGame = false;
            muted = false;

            serializer = new JavaScriptSerializer();
            serializer.RegisterConverters(new[] { new DynamicJsonConverter() });

            sock = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            string[] ep = ip.Split(':');
            IPEndPoint serverAddress = new IPEndPoint(IPAddress.Parse(ep[0]), Convert.ToInt32(ep[1]));
            try
            {
                sock.Connect(serverAddress);
            }
            catch (SocketException E)
            {
                Console.WriteLine("Error during connection :\n" + E.Message + "\nPress enter to exit.");
                Console.ReadLine();
                return;
            }
            new Thread(new ThreadStart(ReceiveData)).Start();

            SendMessage(new LoginMessage(token, training));
            
            Console.ReadLine();
        }

        // Sends a message to the server
        public void SendMessage(ClientMessage msg)
        {
            // Don't forget the \n !
            sock.Send(System.Text.Encoding.UTF8.GetBytes(msg.Serialize() + "\n"), SocketFlags.None);
        }

        // A server message has just been received
        private void Received(dynamic message)
        {
            try
            {
                string type = message.type;
                int error = message.error;
                if (error > 0)
                {
                    Console.WriteLine("Error ("+type+") : "+message.message);
                    return;
                }
                if      (type.Equals("login"))     ReceivedLogin(message.content);
                else if (type.Equals("gamestart")) ReceivedGameStart(message.content);
                else if (type.Equals("gamestate")) ReceivedGameState(message.content);
                else if (type.Equals("gameend"))   ReceivedGameEnd(message.content);
                else if (type.Equals("gamemute"))  ReceivedGameMute(message.content);
                // else Console.WriteLine("Received no-error " + type);
            }
            catch (RuntimeBinderException)
            {
                Console.WriteLine("Error: malformed server message.");
            }
        }

        private void ReceivedLogin(dynamic content)
        {
            try
            {
                string nick = content.nick;
                decimal score = content.score;
                Console.WriteLine("Successfully logged in as " + nick + " (token "+token+"; " + score + " points)");
            }
            catch (RuntimeBinderException)
            {
                Console.WriteLine("Error: malformed server message (login)");
            }
        }
        private void ReceivedGameStart(dynamic content)
        {
            try
            {
                // Init a game
                map = new GameMap(content.map.cols, content.map.rows);
                var hills = content.map.hills;
                for (int i = 0, len = hills.Count; i < len; i++)
                {
                    map.AddHill(hills[i].col, hills[i].row);
                }
                muted = false;
                inGame = true;
                Console.WriteLine("A game just started ("+map.Cols+"x"+map.Rows+")");
            }
            catch (RuntimeBinderException)
            {
                Console.WriteLine("Error: malformed server message (gamestart). " + content);
            }
        }
        private void ReceivedGameState(dynamic content)
        {
            try
            {
                if (!inGame || muted)
                    return;
                map.BeginRound();
                foreach (var obj in content.gameobjects)
                {
                    string type = obj[0].ToLower();
                    int row = obj[1];
                    int col = obj[2];
                    if (type.Equals("a") || type.Equals("b"))
                    {
                        // Living ant
                        int owner = obj[3];
                        if (owner == 0)
                        {
                            map.CheckAnt(col, row);
                        }
                        else
                        {
                            map.AddEnemy(col, row);
                        }
                    }
                    else if (type.Equals("d"))
                    {
                        // Dead ant
                        int owner = obj[3];
                        if (owner == 0)
                        {
                            map.RemoveDeadAnt(col, row);
                        }
                    }
                    else if (type.Equals("w"))
                    {
                        // Wall / water
                        map.RegisterWall(col, row);
                    }
                    else if (type.Equals("f"))
                    {
                        // Food
                        map.AddFood(col, row);
                    }
                }
                map.RemoveUnconfirmedAnts();
                // Uncomment to display the map in the console every turn
                //Console.Write("\n"+map);
                GameActionsMessage msg = new GameActionsMessage();
                map.ComputeMoves(msg);
                SendMessage(msg);
            }
            catch (RuntimeBinderException)
            {
                Console.WriteLine("Error: malformed server message (gamestate). " + content);
            }
        }
        private void ReceivedGameEnd(dynamic content)
        {
            try
            {
                //SendMessage(new LogoutMessage());
                inGame = false;
                string filename = SAVE_DIRECTORY + "\\replay-" + token + "-" + DateTime.Now.Ticks + ".json";
                File.WriteAllText(filename, content.replay.ToString());
                Console.WriteLine("Game ended (" + content.replay.replaydata.cutoff + ") ... Saving replay to " + filename);
            }
            catch (RuntimeBinderException)
            {
                Console.WriteLine("Error: malformed server message (gameend). " + content);
            }
        }
        private void ReceivedGameMute(dynamic content)
        {
            try
            {
                muted = true;
                Console.WriteLine("Muted for this game. Reason : "+content.reason);
            }
            catch (RuntimeBinderException)
            {
                Console.WriteLine("Error: malformed server message (gamemute). " + content);
            }
        }

        /**
         * Function to be run in a thread
         * Constently looking for server messages
         * Deserializing them and passing them to `Received`
         */
        private void ReceiveData()
        {
            string buffer = "";
            while (true)
            {
                if (sock.Connected)
                {
                    if (sock.Poll(10, SelectMode.SelectRead) && sock.Available == 0)
                    {
                        // Connection lost
                        Thread.CurrentThread.Abort();
                    }
                    if (sock.Available > 0)
                    {
                        string messageReceived = null;
                        int newlinePos;
                        int avail;
                        while ((avail = sock.Available) > 0)
                        {
                            try
                            {
                                byte[] msg = new Byte[avail];
                                //Receive data
                                sock.Receive(msg, 0, avail, SocketFlags.None);
                                buffer += System.Text.Encoding.UTF8.GetString(msg);
                                while ((newlinePos = buffer.IndexOf('\n')) > -1)
                                {
                                    messageReceived = buffer.Substring(0, newlinePos);
                                    buffer = buffer.Substring(newlinePos + 1);
                                    try
                                    {
                                        dynamic obj = serializer.Deserialize(messageReceived, typeof(object));
                                        Received(obj);
                                    }
                                    catch (ArgumentException)
                                    {
                                        Console.WriteLine("Error: malformed server message. The message is :\n" + messageReceived);
                                    }
                                }
                            }
                            catch (SocketException E)
                            {
                                Console.WriteLine("Error while receiving a message. " + E.Message);
                            }
                        }
                    }
                }
                Thread.Sleep(10);
            }
        }

        static void Main(string[] args)
        {
            string ip = "127.0.0.1:12345";
            if (args.Length >= 1)
            {
                ip = args[0];
            }
            string token = DEFAULT_TOKEN;
            if (args.Length >= 2)
            {
                token = args[1];
            }
            bool training = DEFAULT_TRAINING;
            if (args.Length >= 3)
            {
                training = Convert.ToBoolean(args[2]);
            }
            new Program(ip, token, training);
        }
    }
}
