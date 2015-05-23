using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BotExample
{
    class GameMap
    {
        public int Cols { get; set; }
        public int Rows { get; set; }
        public HashSet<Coordinates> Hills { get; set; }
        public HashSet<Coordinates> Walls { get; set; }
        // We could use a Dictionary<Coordinates, int> if we wanted to keep track of owners
        public HashSet<Coordinates> Enemies { get; set; }
        public HashSet<Coordinates> Food { get; set; }
        public Dictionary<Coordinates, Ant> MyAnts { get; set; }

        public GameMap(int cols, int rows)
        {
            this.Cols = cols;
            this.Rows = rows;
            this.Hills = new HashSet<Coordinates>();
            this.Walls = new HashSet<Coordinates>();
            this.Enemies = new HashSet<Coordinates>();
            this.Food = new HashSet<Coordinates>();
            this.MyAnts = new Dictionary<Coordinates, Ant>();
        }

        public void AddHill(int col, int row)
        {
            Hills.Add(new Coordinates(col, row));
        }

        // Add a wall to the list (if it is not already in it)
        public void RegisterWall(int col, int row)
        {
            // HashSet implementation will take care of duplicates
            Walls.Add(new Coordinates(col, row));
        }

        // Clear the lists of enemies and food
        public void ClearTempObjects()
        {
            Enemies.Clear();
            Food.Clear();
        }

        public void AddFood(int col, int row)
        {
            Food.Add(new Coordinates(col, row));
        }

        public void AddEnemy(int col, int row)
        {
            Enemies.Add(new Coordinates(col, row));
        }

        // Check if an ant is already registered or not yet, and if not do it
        public void CheckAnt(int col, int row)
        {
            Coordinates pos = new Coordinates(col, row);
            if (!MyAnts.ContainsKey(pos))
            {
                MyAnts.Add(pos, new Ant());
            }
        }

        // When a dead ant is detected, remove it from the list
        public void RemoveDeadAnt(int col, int row)
        {
            Coordinates pos = new Coordinates(col, row);
            if (MyAnts.ContainsKey(pos))
            {
                MyAnts.Remove(pos);
            }
        }

        // Returns the direction of a food or AntDirection.U if there is no accessible food
        private AntDirection GetNearbyFood(Coordinates pos)
        {
            if (Food.Contains(pos.ApplyDirection(AntDirection.N).Normalize(this))) return AntDirection.N;
            else if (Food.Contains(pos.ApplyDirection(AntDirection.S).Normalize(this))) return AntDirection.S;
            else if (Food.Contains(pos.ApplyDirection(AntDirection.E).Normalize(this))) return AntDirection.E;
            else if (Food.Contains(pos.ApplyDirection(AntDirection.W).Normalize(this))) return AntDirection.W;
            return AntDirection.U;
        }

        public void ComputeMoves()
        {
            Random rand = new Random();
            foreach (KeyValuePair<Coordinates, Ant> pair in MyAnts)
            {
                Ant ant = pair.Value;
                Coordinates pos = pair.Key;
                if (ant.HasFood)
                {
                    if (ant.History.Count > 0 && !Hills.Contains(pos))
                    {
                        // Walk back
                        ant.CurrentMove = ant.History.Pop().Reverse();
                        continue;
                    }
                    else
                    {
                        ant.HasFood = false;
                    }
                }
                AntDirection move = GetNearbyFood(pos);
                // If there is accessible food, go there
                if (move == AntDirection.U)
                {
                    List<AntDirection> remaining = new List<AntDirection>() { AntDirection.N, AntDirection.S, AntDirection.E, AntDirection.W };
                    int index;
                    // Try to continue the same way or not ? 50% chance
                    if (ant.History.Count > 0 && rand.NextDouble() < 0.5)
                    {
                        move = ant.History.Peek();
                        remaining.Remove(move);
                    }
                    else
                    {
                        index = rand.Next(4);
                        move = remaining[index];
                        remaining.RemoveAt(index);
                    }
                    int size = 3;
                    do
                    {
                        Coordinates result = pos.ApplyDirection(move).Normalize(this);
                        if (!Walls.Contains(result) && !Enemies.Contains(result))
                        {
                            break;
                        }
                        index = rand.Next(size--);
                        move = remaining[index];
                        remaining.RemoveAt(index);
                    } while (size > 1);
                }
                else
                {
                    ant.HasFood = true;
                }
                ant.CurrentMove = move;
                ant.History.Push(move);
            }
        }

        // Write current moves in msg
        public void GetMoves(GameActionsMessage msg)
        {
            foreach (KeyValuePair<Coordinates, Ant> pair in MyAnts)
            {
                msg.AddMove(pair.Key.Col, pair.Key.Row, pair.Value.CurrentMove.ToString());
            }
        }

        // Change the map according to current ants moves
        public void ApplyMoves()
        {
            Dictionary<Coordinates, Ant> updated = new Dictionary<Coordinates,Ant>();
            foreach (KeyValuePair<Coordinates, Ant> pair in MyAnts)
            {
                Ant ant = pair.Value;
                Coordinates pos = pair.Key.ApplyDirection(ant.CurrentMove).Normalize(this);
                ant.CurrentMove = AntDirection.U;
                updated.Add(pos, ant);
            }
            MyAnts = updated;
        }

        // Display the current map
        public override string ToString()
        {
            string s = "";
            for (int y = 0; y < Rows; y++)
            {
                for (int x = 0; x < Cols; x++)
                {
                    Coordinates pos = new Coordinates(x, y);
                    char c = '_';
                    if (MyAnts.ContainsKey(pos)) c = 'a';
                    else if (Walls.Contains(pos)) c = '#';
                    else if (Enemies.Contains(pos)) c = 'e';
                    else if (Food.Contains(pos)) c = '.';
                    else if (Hills.Contains(pos)) c = '*';
                    s += c;
                }
                s += "\n";
            }
            return s;
        }
    }
}
