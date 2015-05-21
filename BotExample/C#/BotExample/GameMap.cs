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
        public List<Coordinates> Hills { get; set; }
        public HashSet<Coordinates> Walls { get; set; }
        // We could use a Dictionary<Coordinates, int> if we wanted to keep track of owners
        public HashSet<Coordinates> Enemies { get; set; }
        public HashSet<Coordinates> Food { get; set; }
        public Dictionary<Coordinates, Ant> MyAnts { get; set; }

        public GameMap(int cols, int rows)
        {
            this.Cols = cols;
            this.Rows = rows;
            this.Hills = new List<Coordinates>();
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

        public void ComputeMoves()
        {
            Random rand = new Random();
            foreach (KeyValuePair<Coordinates, Ant> pair in MyAnts)
            {
                Ant ant = pair.Value;
                Coordinates pos = pair.Key;
                if (ant.HasFood)
                {
                    if (ant.History.Count > 0)
                    {
                        // Walk back
                        ant.CurrentMove = ant.History.Pop();
                        continue;
                    }
                    else
                    {
                        ant.HasFood = false;
                    }
                }
                List<AntDirection> remaining = new List<AntDirection>() { AntDirection.N, AntDirection.S, AntDirection.E, AntDirection.W };
                int index;
                AntDirection move;
                // Continue the same way or not ?
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
                    Coordinates result = pos.ApplyDirection(move);
                    if (Food.Contains(result))
                    {
                        ant.HasFood = true;
                        break;
                    }
                    if (!Walls.Contains(result) && !Enemies.Contains(result))
                    {
                        break;
                    }
                    index = rand.Next(size--);
                    move = remaining[index];
                    remaining.RemoveAt(index);
                } while (size > 1);
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
    }
}
