/*
 * Stack.cpp
 *
 *  Created on: 08/02/2015
 *      Author: cams7
 */

#ifdef STACK_H_

#include "Stack.h"

#include <stdlib.h>
//#include <stdio.h>

namespace SISBARC {
	template<typename T>
	Stack<T>::Stack() :
	root(NULL) {
		//printf("New Stack\n");
	}

	template<typename T>
	Stack<T>::~Stack() {
		while (!empty()) {
			pop();
		}

		//printf("Delete Stack\n");
	}

	template<typename T>
	Node<T>* Stack<T>::getRoot() const {
		return root;
	}

	template<typename T>
	void Stack<T>::setRoot(Node<T>* const root) {
		this->root = root;
	}

	template<typename T>
	void Stack<T>::push(T* const/*&*/element) {
		if (!empty()) {
			Node<T>* previous;
			Node<T>* next;

			previous = getRoot();
			next = getRoot()->getNext();

			while (next != NULL) {
				previous = next;
				next = next->getNext();
			}

			next = new Node<T>(element);
			previous->setNext(next);
			next->setPrevious(previous);
		} else {
			setRoot(new Node<T>(element));
		}
	}

	template<typename T>
	void Stack<T>::pop() {
		if (!empty()) {
			Node<T>* previous;
			Node<T>* next;

			previous = getRoot();
			next = getRoot()->getNext();

			while (next != NULL) {
				previous = next;
				next = next->getNext();
			}

			if (previous->getPrevious() != NULL) {
				previous->getPrevious()->setNext(NULL);
			} else if (previous->getNext() == NULL) {
				setRoot(NULL);
			}
			delete previous;
		}
	}

	template<typename T>
	T* Stack<T>::top() const {
		if (!empty()) {
			Node<T>* previous;
			Node<T>* next;

			previous = getRoot();
			next = getRoot()->getNext();

			while (next != NULL) {
				previous = next;
				next = next->getNext();
			}

			return previous->getElement();
		}

		return NULL;
	}

	template<typename T>
	bool Stack<T>::empty() const {
		return (getRoot() == NULL);
	}

} /* namespace SISBARC */

#endif /* STACK_H_ */
