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

namespace BotExample
{
    class Program
    {
        /* CHANGE SETTINGS HERE */
        private const string TOKEN = "abc";
        private const bool TRAINING = true;

        private Socket sock;
        private JavaScriptSerializer serializer;

        private bool inGame;
        private bool muted;
        private GameMap map;

        public Program()
        {
            inGame = false;
            muted = false;

            serializer = new JavaScriptSerializer();
            serializer.RegisterConverters(new[] { new DynamicJsonConverter() });

            sock = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            IPEndPoint serverAddress = new IPEndPoint(IPAddress.Parse("127.0.0.1"), 12345);
            sock.Connect(serverAddress);
            new Thread(new ThreadStart(ReceiveData)).Start();

            SendMessage(new LoginMessage(TOKEN, TRAINING));
            
            // Console.ReadLine();
        }

        public void SendMessage(ClientMessage msg)
        {
            sock.Send(System.Text.Encoding.UTF8.GetBytes(msg.Serialize() + "\n"), SocketFlags.None);
        }

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
                dynamic content = message.content;
                if      (type.Equals("login"))     ReceivedLogin(content);
                else if (type.Equals("gamestart")) ReceivedGameStart(content);
                else if (type.Equals("gamestate")) ReceivedGameState(content);
                else if (type.Equals("gameend"))   ReceivedGameEnd(content);
                else if (type.Equals("gamemute"))  ReceivedGameMute(content);
            }
            catch (RuntimeBinderException)
            {
                Console.WriteLine("Error: malformed server message");
            }
        }

        private void ReceivedLogin(dynamic content)
        {
            try
            {
                string nick = content.nick;
                int score = content.score;
                Console.WriteLine("Successfully logged in as " + nick + " (" + score + " points)");
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
                map = new GameMap(content.map.cols, content.map.rows);
                var hills = content.map.hills;
                for (int i = 0, len = hills.Count; i < len; i++)
                {
                    map.AddHill(hills[i].col, hills[i].row);
                }
                muted = false;
                inGame = true;
            }
            catch (RuntimeBinderException)
            {
                Console.WriteLine("Error: malformed server message (gamestart)");
            }
        }
        private void ReceivedGameState(dynamic content)
        {
            try
            {
                if (!inGame || muted)
                    return;
                map.ClearTempObjects();
                foreach (var obj in content.gameobjects)
                {
                    string type = obj[0];
                    int row = obj[1];
                    int col = obj[2];
                    if (type.Equals("a"))
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
                map.ComputeMoves();
                GameActionsMessage msg = new GameActionsMessage();
                map.GetMoves(msg);
                SendMessage(msg);
            }
            catch (RuntimeBinderException)
            {
                Console.WriteLine("Error: malformed server message (gamestate)");
            }
        }
        private void ReceivedGameEnd(dynamic content)
        {
            try
            {
                inGame = false;
                Console.WriteLine("Game ended ... score=" + content.gamescore + ", new bot score : " + content.score);
            }
            catch (RuntimeBinderException)
            {
                Console.WriteLine("Error: malformed server message (gameend)");
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
                Console.WriteLine("Error: malformed server message (gamemute)");
            }
        }

        /**
         * Function ran in a thread
         * Constently looking for server messages
         * Deserializing them and passing them to `Received`
         */
        private void ReceiveData()
        {
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
                        while (sock.Available > 0)
                        {
                            try
                            {
                                byte[] msg = new Byte[sock.Available];
                                //Receive data
                                sock.Receive(msg, 0, sock.Available, SocketFlags.None);
                                messageReceived = System.Text.Encoding.UTF8.GetString(msg).Trim();
                                //Console.WriteLine(messageReceived);
                                dynamic obj = serializer.Deserialize(messageReceived, typeof(object));
                                Received(obj);
                            }
                            catch (SocketException E)
                            {
                                Console.WriteLine("Error while receiving a message.");
                            }
                        }
                    }
                }
                Thread.Sleep(10);
            }
        }

        static void Main(string[] args)
        {
            new Program();
        }
    }
}
