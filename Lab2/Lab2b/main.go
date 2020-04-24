package main

import "fmt"

func ivanov_work(data_chan chan int)  {
	for i:=0; i<20; i++{
		data_chan<-1
		fmt.Println("Ivanov -> Petrov")
	}
}

func petrov_work(data_chan1, data_chan2 chan int)  {
	for i:=0; i<20; i++{
		<-data_chan1
		fmt.Println("Petrov <- Ivanov")
		data_chan2<-1
		fmt.Println("Petrov -> Shvab")
	}
}

func shvab_work(data_chan chan int)  {
	for i:=0; i<20; i++{
		<-data_chan
		fmt.Println("Shvab <- Petrov")
	}
}
func main() {
	var chan1 chan int = make(chan int)
	var chan2 chan int = make(chan int)
	go ivanov_work(chan1)
	go petrov_work(chan1,chan2)
	go shvab_work(chan2)

	fmt.Scanln()
}
