/*
 * Copyright 2009 Inspire-Software.com
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
import { Component,  Input, Output, EventEmitter, OnInit } from '@angular/core';
import { LogUtil } from './../log/index';

/**
 * Shows a bootstrap modal dialog.
 * Set the body of the dialog by adding content to the modal tag: <cw-modal>content here</cw-modal>.
 */
@Component({
  selector: 'cw-modal',
  templateUrl: './modal.component.html',
  moduleId: module.id,
})
export class ModalComponent implements OnInit {

  @Input() title: string;
  @Input() footer: string;
  @Input() cancelLabel: string = 'Cancel';
  @Input() disableCancel: boolean = false;
  @Input() positiveLabel: string = 'OK';
  @Input() valid: boolean = false;

  /**
   * Fires an event when the modal is closed. The argument indicated how it was closed.
   * @type {EventEmitter<ModalResult>}
   */
  @Output() closed: EventEmitter<ModalResult> = new EventEmitter<ModalResult>();
  /**
   * Fires an event when the modal is ready with a pointer to the modal.
   * @type {EventEmitter<ModalComponent>}
   */
  @Output() loaded: EventEmitter<ModalComponent> = new EventEmitter<ModalComponent>();

  showModal: boolean = false;

  constructor() {
    LogUtil.debug('ModalComponent = ' + this.showModal);
  }

  ngOnInit() {
    this.loaded.next(this);
    LogUtil.debug('ModalComponent inited');
  }


  /**
   * Shows the modal. There is no method for hiding. This is done using actions of the modal itself.
   */
  show() {
    this.showModal = true;
  }

  positiveAction() {
    if (this.valid) {
      LogUtil.debug('ModalComponent sending positive event');
      this.closed.next({
        action: ModalAction.POSITIVE
      });
      this.showModal = false;
    }
    return false;
  }

  cancelAction() {
    LogUtil.debug('ModalComponent sending close event');
    this.closed.next({
      action: ModalAction.CANCEL
    });
    this.showModal = false;
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
