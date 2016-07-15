import {Component,  Input, Output, EventEmitter, OnInit} from 'angular2/core';

/**
 *
 *
 *
 * Warning this is ^C^V from https://github.com/valor-software/ng2-bootstrap/issues/29
 * TODO replace with original implementation when in will be ready for usage
 *
 *
 *
 * Shows a bootstrap modal dialog.
 * Set the body of the dialog by adding content to the modal tag: <modal>content here</modal>.
 */
@Component({
  selector: 'modal',
  templateUrl: './modal.html',
  moduleId: module.id
})
export class Modal implements OnInit {

  @Input() title: string;
  @Input() cancelLabel: string = 'Cancel';
  @Input() positiveLabel: string = 'OK';

  /**
   * Fires an event when the modal is closed. The argument indicated how it was closed.
   * @type {EventEmitter<ModalResult>}
   */
  @Output() closed: EventEmitter<ModalResult> = new EventEmitter<ModalResult>();
  /**
   * Fires an event when the modal is ready with a pointer to the modal.
   * @type {EventEmitter<Modal>}
   */
  @Output() loaded: EventEmitter<Modal> = new EventEmitter<Modal>();

  showModal: boolean = false;

  constructor() {
    console.log('showModal = ' + this.showModal);
  }

  ngOnInit() {
    this.loaded.next(this);
    console.log('modal inited');
  }


  /**
   * Shows the modal. There is no method for hiding. This is done using actions of the modal itself.
   */
  show() {
    this.showModal = true;
  }

  positiveAction() {
    this.showModal = false;
    this.closed.next({
      action: ModalAction.POSITIVE
    });
    return false;
  }

  cancelAction() {
    console.log('sending close event');
    this.showModal = false;
    this.closed.next({
      action: ModalAction.CANCEL
    });
    return false;
  }
}

/**
 * The possible reasons a modal has been closed.
 */
export enum ModalAction { POSITIVE, CANCEL }
/**
 * Models the result of closing a modal dialog.
 */
export interface ModalResult {
  action: ModalAction;
}
